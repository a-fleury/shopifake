@echo off
REM Pre-commit linting script for chatbot service (Windows)
REM Expected to be run from the microservice folder: backend\chatbot-service

REM Try to activate local virtual environment
if exist ".venv\Scripts\activate.bat" (
  call .venv\Scripts\activate.bat
) else (
  echo ⚠️  No local .venv found. Consider creating one with: python -m venv .venv
)

echo ===== Ruff format =====
ruff format .
if %ERRORLEVEL% NEQ 0 goto :fail

echo ===== Ruff check =====
ruff check .
if %ERRORLEVEL% NEQ 0 goto :fail

echo ===== Mypy =====
mypy .
if %ERRORLEVEL% NEQ 0 goto :fail

@echo.
@echo ✅ All checks passed! Ready to commit.
exit /b 0

:fail
@echo.
@echo ❌ Checks failed. Fix issues above and re-run.
exit /b 1
