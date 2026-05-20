#!/bin/sh
# Cài pre-commit hook cho Mac/Linux
# Chạy: chmod +x scripts/install-hook.sh && ./scripts/install-hook.sh

HOOK_FILE=".git/hooks/pre-commit"

echo "Đang cài pre-commit hook..."

if [ ! -d ".git/hooks" ]; then
    echo "[ERROR] Không tìm thấy .git/hooks. Chắc chắn bạn đang ở root của git repo chưa?"
    exit 1
fi

cat > "$HOOK_FILE" << 'EOF'
#!/bin/sh
python "$(git rev-parse --show-toplevel)/scripts/pre-commit-review.py"
EOF

chmod +x "$HOOK_FILE"

echo "[OK] Hook đã được cài tại: $HOOK_FILE"
echo ""
echo "Bước tiếp theo:"
echo "  1. Đảm bảo OPENAI_API_KEY đã được set trong .env hoặc environment variable"
echo "  2. Cài openai package: pip install openai"
echo "  3. Thử commit để kiểm tra hook hoạt động"
echo ""
echo "Để bỏ qua review 1 lần: SKIP_AI_REVIEW=1 git commit -m '...'"
echo "Để bỏ qua hoàn toàn:    git commit --no-verify"
