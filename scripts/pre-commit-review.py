#!/usr/bin/env python3
"""
Pre-commit AI Code Review
Gọi OpenAI API để review staged changes trước khi commit.
- Critical issues  → block commit
- Warning/Suggestion → in ra, cho commit tiếp
- API lỗi / không có key → warn, cho commit tiếp (không block)

Bypass: SKIP_AI_REVIEW=1 git commit
        hoặc:  git commit --no-verify
"""

import subprocess
import sys
import os
import json

# Windows console UTF-8 support
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    sys.stderr.reconfigure(encoding="utf-8", errors="replace")

# --- Cấu hình ---
MODEL = "gpt-4o"
MAX_DIFF_CHARS = 8000   # giới hạn để tránh tốn token quá nhiều
TIMEOUT_SECONDS = 30
REVIEWED_EXTENSIONS = {
    ".java", ".py", ".js", ".ts", ".jsx", ".tsx",
    ".sql", ".properties", ".yml", ".yaml"
}

# --- Load .env nếu có python-dotenv ---
try:
    from dotenv import load_dotenv
    load_dotenv()
except ImportError:
    pass  # không bắt buộc, dùng system env var cũng được


def get_staged_diff() -> str:
    """Lấy diff của các file staged, chỉ lấy extension liên quan."""
    result = subprocess.run(
        ["git", "diff", "--cached", "--diff-filter=ACMR", "--name-only"],
        capture_output=True, text=True, encoding="utf-8", errors="replace"
    )
    staged_files = [
        f for f in result.stdout.strip().splitlines()
        if any(f.endswith(ext) for ext in REVIEWED_EXTENSIONS)
    ]

    if not staged_files:
        return ""

    result = subprocess.run(
        ["git", "diff", "--cached", "--diff-filter=ACMR", "--"] + staged_files,
        capture_output=True, text=True, encoding="utf-8", errors="replace"
    )
    return result.stdout or ""


def review_with_openai(diff: str, api_key: str) -> dict:
    """Gửi diff lên OpenAI, nhận kết quả review dạng JSON."""
    try:
        from openai import OpenAI
    except ImportError:
        print("⚠️  [AI Review] openai chưa được cài. Skipping review.")
        print("   Cài bằng: pip install openai")
        sys.exit(0)

    client = OpenAI(api_key=api_key, timeout=TIMEOUT_SECONDS)

    prompt = f"""You are a senior code reviewer. Review the following git diff for issues.

Focus on:
- Security vulnerabilities (SQL injection, XSS, hardcoded secrets, auth bypass, missing auth check)
- Logic errors and incorrect behavior
- Null pointer / exception risks
- Missing @Transactional on write operations
- Performance issues (N+1 queries, DB calls inside loops)
- Sensitive data exposed in logs or API responses

Severity levels:
- Critical: must fix before commit (security hole, data loss risk, app crash)
- Warning: should fix soon (code smell, potential bug, bad practice)
- Suggestion: nice to have (minor improvement)

Return ONLY valid JSON in this exact format (no markdown, no extra text):
{{
  "summary": "one-line overall assessment",
  "issues": [
    {{
      "severity": "Critical|Warning|Suggestion",
      "file": "filename",
      "problem": "what is wrong",
      "suggestion": "how to fix it"
    }}
  ]
}}

If no issues found, return: {{"summary": "No issues found", "issues": []}}

Git diff:
{diff}"""

    response = client.chat.completions.create(
        model=MODEL,
        messages=[{"role": "user", "content": prompt}],
        response_format={"type": "json_object"},
    )

    return json.loads(response.choices[0].message.content)


def print_issues(issues: list, severity: str, icon: str, label: str):
    filtered = [i for i in issues if i.get("severity") == severity]
    if not filtered:
        return
    print(f"\n{icon} {label}:")
    for issue in filtered:
        print(f"   • [{issue.get('file', '?')}] {issue.get('problem', '')}")
        if issue.get("suggestion"):
            print(f"     → {issue.get('suggestion')}")


def main():
    # Bypass flag
    if os.environ.get("SKIP_AI_REVIEW") == "1":
        print("⏭️  [AI Review] Skipped via SKIP_AI_REVIEW=1")
        sys.exit(0)

    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        print("⚠️  [AI Review] OPENAI_API_KEY chưa được set. Skipping review.")
        print("   Thêm vào file .env hoặc set environment variable.")
        sys.exit(0)

    diff = get_staged_diff()

    if not diff.strip():
        sys.exit(0)  # không có file code nào thay đổi

    if len(diff) > MAX_DIFF_CHARS:
        print(f"⚠️  [AI Review] Diff quá lớn ({len(diff):,} chars > {MAX_DIFF_CHARS:,}). Skipping.")
        sys.exit(0)

    print("🔍 [AI Review] Đang review staged changes...")

    try:
        result = review_with_openai(diff, api_key)
    except Exception as e:
        print(f"⚠️  [AI Review] Lỗi khi gọi API: {e}")
        print("   Cho phép commit tiếp (không block vì lỗi mạng/API).")
        sys.exit(0)

    issues = result.get("issues", [])
    summary = result.get("summary", "")
    critical = [i for i in issues if i.get("severity") == "Critical"]

    if not issues:
        print(f"✅ [AI Review] {summary}")
        sys.exit(0)

    print(f"\n📋 [AI Review] {summary}")

    print_issues(issues, "Critical",    "🔴", "CRITICAL — phải fix trước khi commit")
    print_issues(issues, "Warning",     "🟡", "WARNING — nên xem lại")
    print_issues(issues, "Suggestion",  "🟢", "SUGGESTION")

    if critical:
        print("\n❌ [AI Review] Commit bị chặn do có Critical issue.")
        print("   Sửa xong rồi commit lại, hoặc dùng: SKIP_AI_REVIEW=1 git commit")
        sys.exit(1)
    else:
        print("\n⚠️  [AI Review] Có warning nhưng commit được phép tiếp tục.")
        sys.exit(0)


if __name__ == "__main__":
    main()
