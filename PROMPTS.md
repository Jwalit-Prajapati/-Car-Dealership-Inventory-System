# Prompts and Interactions

## 2026-07-22T09:14:17+05:30

**User Prompt:**
Act as a Principal Software Engineer and Technical Architect.

I am building the backend for a Vehicle Dealership Management System using Test Driven Development (TDD). Your task is to create a COMPLETE implementation roadmap that follows real industry practices.

=========================
IMPORTANT RULES
=========================
1. The roadmap MUST strictly follow Test Driven Development.
   Every story must follow:
   Red
   - Write failing tests first.
   - Commit.
   Green
   - Write only enough production code to make tests pass.
   - Commit.
   Refactor
   - Improve the code while keeping tests green.
   - Commit.
... (Full prompt related to TDD Backend for Vehicle Dealership Management System) ...

**AI Response:**
I have created the requested comprehensive TDD implementation roadmap artifact. The roadmap includes 6 phases and 16 detailed stories, breaking down the project setup, user management, authentication, vehicle management, inventory handling, and admin dashboard analytics, all while strictly adhering to the Red-Green-Refactor cycle and requiring specific Git commit messages for each step. 

You can find the artifact in the system containing the full implementation steps for the vehicle dealership backend.

## 2026-07-22T09:59:22+05:30

**User Prompt:**
What project instructions are currently active?

**AI Response:**
Based on the `AGENTS.md` file in your repository, the following project instructions are currently active:

1. **Git Commit Messages:**
   - Use the Conventional Commits format.
   - Insert exactly two blank lines after the commit message.
   - Add a truthful AI usage disclosure detailing how AI was used for that specific change.

2. **PROMPTS.md Maintenance:**
   - Maintain a `PROMPTS.md` file in the project root.
   - Append every development-related interaction to `PROMPTS.md`, keeping entries in chronological order.
   - Include the timestamp, User Prompt, and AI Response.
   - Never overwrite existing content.

*(I have updated `PROMPTS.md` with this interaction in accordance with the rules!)*

## 2026-07-22T10:16:09+05:30

**User Prompt:**
Phase 1 – Project Setup and Infrastructure
Story 1 – Package Structure and Common Constants
Create the required package structure and write a failing unit test for AppConstants (RED phase). Stop and wait for approval.

**AI Response:**
I have created the failing unit test `AppConstantsTest.java` using Java reflection to verify that `AppConstants` has a private constructor that throws `UnsupportedOperationException`. The test fails because `AppConstants` doesn't exist yet, which causes a compilation error. I have committed this RED phase with the required commit message format. I am now waiting for your approval to proceed to the GREEN phase.


## 2026-07-22T10:26:14+05:30

**User Prompt:**
I mean commited in git hub?

**AI Response:**
I had only committed it locally at first. I have now pushed the commit to GitHub (origin/main). I will make sure to push to GitHub after each phase if you prefer!

## 2026-07-22T10:29:29+05:30

**User Prompt:**
proceed with Green phase and Note that for further process write commit message like this

git commit -m "feat: Implement user registration endpoint 
 
Used an AI assistant to generate the initial boilerplate for the 
controller and service, then manually added validation logic. 
 
Co-authored-by: AI Tool Name <AI@users.noreply.github.com>"

**AI Response:**
I have completed the GREEN phase. I created the base package directories and added .gitkeep so they are tracked by Git. I also created a minimal AppConstants.java utility class to satisfy the unit test. I ran the Maven tests, which all passed successfully.

I have also committed and pushed the changes to GitHub using your requested commit message structure containing the \Co-authored-by\ attribution.

Are you ready for me to proceed to the REFACTOR phase?
