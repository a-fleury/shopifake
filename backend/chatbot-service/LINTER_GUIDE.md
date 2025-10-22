# Linter Quick Reference Guide

## 🚀 Common Commands

### Run Ruff Formatter (auto-fixes formatting)
```bash
.venv/bin/ruff format .
```

### Run Ruff Linter (check for issues)
```bash
.venv/bin/ruff check .
```

### Run Ruff Linter with Auto-fix
```bash
.venv/bin/ruff check --fix .
```

### Run Mypy Type Checker
```bash
.venv/bin/mypy .
```

### Run All Tools in Order
```bash
.venv/bin/ruff format . && .venv/bin/ruff check . && .venv/bin/mypy .
```

---

## 📖 Understanding Rule Codes

When Ruff shows an error, it includes a code like `E501` or `F401`. Here's what they mean:

### Common Error Codes

| Code | Description | Example |
|------|-------------|---------|
| **E501** | Line too long | Line exceeds 100 characters |
| **F401** | Unused import | `import json` but never used |
| **F841** | Unused variable | `x = 5` but never used |
| **E402** | Import not at top | Import after other code |
| **W291** | Trailing whitespace | Spaces at end of line |
| **I001** | Import order wrong | Wrong import section order |
| **N802** | Function name not lowercase | `def MyFunction()` should be `my_function()` |
| **B006** | Mutable default argument | `def func(x=[]):` is dangerous |
| **UP** | Use modern syntax | Use `list[str]` not `List[str]` |

---

## 🔧 Temporarily Ignoring Errors

### Ignore single line (Ruff)
```python
result = some_long_function_call(arg1, arg2, arg3, arg4, arg5)  # noqa: E501
```

### Ignore specific error on line (Ruff)
```python
import joblib  # noqa: F401
```

### Ignore type error (Mypy)
```python
value = get_dynamic_value()  # type: ignore[arg-type]
```

### Ignore entire file (add at top)
```python
# ruff: noqa
```

---

## 📊 Check Specific Files

### Lint single file
```bash
.venv/bin/ruff check app.py
```

### Format single file
```bash
.venv/bin/ruff format app.py
```

### Type check single file
```bash
.venv/bin/mypy app.py
```

---

## 🎯 Rule Categories Reference

| Category | Code | What it does |
|----------|------|--------------|
| **pycodestyle** | E, W | PEP 8 style enforcement |
| **Pyflakes** | F | Detect errors like unused imports |
| **isort** | I | Sort and organize imports |
| **pep8-naming** | N | Check naming conventions |
| **pyupgrade** | UP | Modernize Python code |
| **flake8-bugbear** | B | Find common bugs |
| **flake8-comprehensions** | C4 | Better comprehensions |
| **flake8-simplify** | SIM | Simplify complex code |
| **Ruff-specific** | RUF | Ruff's own improvements |

---

## 💡 Best Practices

1. **Run formatter first**, then linter
   - Formatter fixes style automatically
   - Linter checks logic and bugs

2. **Fix errors incrementally**
   - Don't try to fix everything at once
   - Tackle one category at a time

3. **Use `--fix` for auto-fixable issues**
   - Many issues can be fixed automatically
   - Review changes before committing

4. **Add type hints gradually**
   - Start with function signatures
   - Add return types
   - Eventually add parameter types

5. **Run linters before committing**
   - Catch issues early
   - Keep codebase clean

---

## 🆘 Troubleshooting

### "Command not found: ruff"
**Solution:** Activate virtual environment first
```bash
source .venv/bin/activate
# Then run: ruff check .
```

Or use full path:
```bash
.venv/bin/ruff check .
```

### Too many errors
**Solution:** Fix categories one at a time
```bash
# Fix only import errors
.venv/bin/ruff check --select I --fix .

# Fix only naming
.venv/bin/ruff check --select N --fix .
```

### Mypy complains about missing types
**Solution:** Add to pyproject.toml:
```toml
[[tool.mypy.overrides]]
module = "library_name.*"
ignore_missing_imports = true
```

---

## 📚 Learn More

- Ruff Rules: https://docs.astral.sh/ruff/rules/
- Mypy Docs: https://mypy.readthedocs.io/
- PEP 8 Style Guide: https://pep8.org/
