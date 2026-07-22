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

# Testing Policy

Default testing style:

- Unit tests only.
- Fast and isolated.
- No unnecessary Spring context.

Preferred tools:

- JUnit 5
- AssertJ
- Mockito

Allowed Spring testing annotations:

- @DataJpaTest (repository tests only)

Do NOT use the following unless I explicitly request them:

- @SpringBootTest
- @WebMvcTest
- Full Spring Context
- Integration tests
- End-to-end tests

Repository tests are the only exception and may use:

- @DataJpaTest
- Testcontainers
- Flyway

Use Testcontainers only when persistence behaviour or database constraints must be verified.

---

# Implementation Guidelines

Always implement the smallest change necessary.

Do not:

- Add future features.
- Over-engineer.
- Introduce unnecessary abstractions.
- Add dependencies unless required.
- Modify unrelated files.

Keep production code simple and incremental.

---

# Git Workflow

For every story:

RED
→ Wait for approval
→ Commit

GREEN
→ Wait for approval
→ Commit

REFACTOR
→ Wait for approval
→ Commit

Never create commits automatically.

Only commit after I explicitly approve the current phase or instruct you to proceed.
Follow the Git Commit Message policy above.

