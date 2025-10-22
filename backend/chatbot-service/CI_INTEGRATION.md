# GitHub Actions CI/CD Integration for Ruff + Mypy

This guide explains how the linting workflow integrates into your CI/CD pipeline.

## 📁 Workflow File

Located at: `.github/workflows/chatbot-lint.yml`

---

## 🔍 How It Works

### **Trigger Conditions**
```yaml
on:
  push:
    branches: [ main, develop, feature/chatbot ]
    paths:
      - 'backend/chatbot-service/**'
```

**Meaning:** The workflow runs when:
- ✅ You push code to `main`, `develop`, or `feature/chatbot` branches
- ✅ Changes are in the `backend/chatbot-service/` directory
- ✅ Someone creates a pull request affecting these files

**Why `paths` filter?** Saves CI minutes by only running when chatbot code changes

---

### **Job Steps Explained**

#### **1. Checkout Code**
```yaml
- uses: actions/checkout@v4
```
Downloads your repository code to the CI runner

#### **2. Setup Python**
```yaml
- uses: actions/setup-python@v5
  with:
    python-version: '3.12'
    cache: 'pip'
```
- Installs Python 3.12 (matching your local setup)
- **`cache: 'pip'`** → Caches dependencies to speed up future runs

#### **3. Install Dependencies**
```yaml
- run: |
    pip install -r requirements.txt
    pip install -r requirements-dev.txt
```
Installs both production and development dependencies

#### **4. Run Checks**
Three separate steps for clarity:
- **Ruff Format Check** → Fails if code isn't formatted
- **Ruff Linter** → Fails if code has linting issues
- **Mypy Type Check** → Fails if type errors exist

---

## 🎯 CI Behavior

### **What Happens on Failure?**

If any check fails:
- ❌ The workflow fails (red X on GitHub)
- 🚫 Pull requests are blocked from merging
- 📧 You get a notification
- 📝 Error details shown in GitHub UI

### **What Happens on Success?**

If all checks pass:
- ✅ Green checkmark on GitHub
- ✅ Pull requests can be merged
- 📊 Shows in PR status checks

---

## 🚀 Advanced Options

### **Option 1: Add Annotations**

Show linting errors directly in GitHub PR files:

```yaml
- name: Run Ruff Linter
  working-directory: backend/chatbot-service
  run: |
    ruff check --output-format=github .
```

**Result:** Errors appear as comments on the exact lines in your PR!

### **Option 2: Auto-fix on CI (Advanced)**

Automatically fix issues and commit them:

```yaml
- name: Run Ruff Auto-fix
  run: |
    ruff check --fix .
    ruff format .

- name: Commit changes
  uses: stefanzweifel/git-auto-commit-action@v5
  with:
    commit_message: "style: auto-fix linting issues"
```

⚠️ **Use carefully:** Can create unexpected commits

### **Option 3: Continue on Error**

Run all checks even if one fails:

```yaml
- name: Run Ruff Linter
  continue-on-error: true
  run: ruff check .
```

Useful for gathering all errors at once

### **Option 4: Upload Results as Artifacts**

Save linting reports:

```yaml
- name: Generate Ruff Report
  run: |
    ruff check --output-format=json > ruff-report.json

- name: Upload Report
  uses: actions/upload-artifact@v4
  with:
    name: lint-reports
    path: ruff-report.json
```

---

## 📊 Branch Protection Rules

To enforce linting on PRs:

1. Go to **GitHub repo → Settings → Branches**
2. Add rule for `main` branch
3. Enable "Require status checks to pass"
4. Select "Ruff & Mypy Checks"

**Result:** PRs can't merge until linting passes! 🔒

---

## 🔧 Local Pre-commit vs CI

| Check Type | When | Purpose |
|------------|------|---------|
| **Pre-commit hook** | Before git commit (local) | Catch issues early |
| **CI Workflow** | After push (remote) | Enforce standards team-wide |

**Best practice:** Use both!
- Pre-commit catches most issues locally
- CI is the final safety net

---

## 📈 Monitoring

### **View CI Results**

1. Go to **Actions** tab in GitHub
2. Click on workflow run
3. View each step's output
4. Download logs if needed

### **CI Badge (Optional)**

Add to your README:

```markdown
[![Lint](https://github.com/a-fleury/shopifake/actions/workflows/chatbot-lint.yml/badge.svg)](https://github.com/a-fleury/shopifake/actions/workflows/chatbot-lint.yml)
```

Shows build status: ![Passing](https://img.shields.io/badge/lint-passing-brightgreen)

---

## 💡 Best Practices

1. **Run locally first:** Don't rely on CI to catch everything
2. **Fast feedback:** Keep CI fast (<2 minutes)
3. **Fail fast:** Stop on first error to save time
4. **Clear messages:** Make errors obvious
5. **Cache dependencies:** Speed up workflow with pip cache

---

## 🆘 Troubleshooting

### **"Command not found: ruff"**
**Fix:** Ensure `requirements-dev.txt` is installed in workflow

### **"Cache not working"**
**Fix:** Check `cache-dependency-path` matches your requirements files

### **"Tests always pass locally but fail in CI"**
**Fix:** Python version mismatch - ensure CI uses same Python version

---

## 🔄 Workflow Comparison

### **Simple (Current)**
- ✅ Basic linting checks
- ✅ Easy to understand
- ✅ Fast (~1-2 minutes)

### **Advanced (Optional)**
- ✅ All of the above
- ✅ Auto-fix capability
- ✅ Detailed reports
- ✅ PR annotations
- ⚠️ More complex

**Recommendation:** Start simple, add features as needed

---

## 📝 Next Steps

1. ✅ Push workflow file to GitHub
2. ✅ Create a test PR to see it in action
3. ✅ (Optional) Add branch protection rules
4. ✅ (Optional) Set up pre-commit hooks locally

The workflow is ready to use! It will automatically run on your next push.
