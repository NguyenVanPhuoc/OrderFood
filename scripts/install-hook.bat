@echo off
REM Cài pre-commit hook cho Windows
REM Chạy file này 1 lần sau khi clone repo

set HOOK_DIR=%~dp0..\\.git\\hooks
set HOOK_FILE=%HOOK_DIR%\\pre-commit

echo Đang cài pre-commit hook...

REM Kiểm tra folder .git/hooks tồn tại
if not exist "%HOOK_DIR%" (
    echo [ERROR] Không tìm thấy .git/hooks. Chắc chắn bạn đang ở trong git repo chưa?
    exit /b 1
)

REM Tạo file pre-commit hook (dùng bash để tránh BOM)
bash -c "printf '#!/bin/sh\npython \"$(git rev-parse --show-toplevel)/scripts/pre-commit-review.py\"\n' > .git/hooks/pre-commit && chmod +x .git/hooks/pre-commit"

echo [OK] Hook đã được cài tại: %HOOK_FILE%
echo.
echo Bước tiếp theo:
echo   1. Đảm bảo OPENAI_API_KEY đã được set trong .env hoặc environment variable
echo   2. Cài openai package: pip install openai
echo   3. Thử commit để kiểm tra hook hoạt động
echo.
echo Để bỏ qua review 1 lần: SKIP_AI_REVIEW=1 git commit -m "..."
echo Để bỏ qua hoàn toàn:    git commit --no-verify
