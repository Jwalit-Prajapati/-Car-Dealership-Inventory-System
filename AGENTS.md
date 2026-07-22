# AGENTS.md

## AI Usage Policy

For this repository, always follow these rules.

### 1. Git Commit Messages

Whenever I ask you to generate a commit message:

- Use the Conventional Commits format.
- After the commit message, insert exactly two blank lines.
- Then add a truthful AI usage disclosure describing how AI was used for that specific change.

Example:

feat(auth): implement user registration endpoint


Used an AI assistant to generate the initial controller and service boilerplate, then manually implemented validation, business logic, and tests.

### 2. PROMPTS.md

Maintain a `PROMPTS.md` file in the project root.

Whenever we have a development-related conversation:

- Append the latest interaction to `PROMPTS.md`.
- Include:
    - Timestamp (if available)
    - User Prompt
    - AI Response
- Never overwrite existing content.
- Keep entries in chronological order.

If `PROMPTS.md` does not exist, create it.

## Originality Policy

- Never copy or closely reproduce code from GitHub repositories, Stack Overflow, blogs, tutorials, or other public sources.
- Use official language/framework documentation only as a reference for APIs and behavior.
- Generate all production and test code from first principles based on the requirements.
- If a generated solution appears similar to an external source, rewrite it from scratch before presenting it.
- Prefer simple, idiomatic, and original implementations over reproducing common examples.