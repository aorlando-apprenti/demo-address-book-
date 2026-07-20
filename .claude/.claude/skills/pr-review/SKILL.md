---
name: pr-review
description: Performs static code audits, style verification, security assessments, and architectural compliance checks.
---
# Production Gate & Review Playbook

When reviewing a feature or running `/pr-review`:
1. **Audit Diff:** Review newly changed/added files against the `ARCHITECTURE.md` blueprint.
2. **Verify Integrity:** Look for security issues (exposed variables, SQL injection vectors, poor validation), structural complexity, and missing edge cases.
3. **Status Output:** Provide constructive feedback. If changes are needed, list them as blocks. If perfect, issue an explicit approval stamp: `[APPROVED FOR PRODUCTION]`.
