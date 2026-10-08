# Cashback Rewards

**Learn to build production-quality Spring Boot APIs using AI as your pair programmer.**

---

## What You'll Build

A Spring Boot microservice that calculates cashback rewards for customer purchases at partner merchants. Along the way, you'll learn to:

- **Discover requirements with AI** — use Example Mapping and a custom `/discover` command to turn user stories into precise specifications with rules, examples, and counter-examples
- **Drive development with specs** — write acceptance tests directly from specs, then implement features using red-green-refactor TDD cycles
- **Configure Claude Code for your project** — set up CLAUDE.md, path-scoped architecture rules, and custom commands that make Claude a project-aware pair programmer
- **Enforce architecture automatically** — use rules files and hooks to guarantee hexagonal architecture compliance, not just suggest it
- **Build with confidence** — every feature is backed by acceptance tests, domain unit tests, controller tests, and repository tests

## Prerequisites

- **Java 25** — download from [Adoptium](https://adoptium.net/) or [SDKMAN](https://sdkman.io/)
- **Maven** — included via Maven Wrapper (`./mvnw`), no separate install needed
- **Docker** (optional) — only needed for PostgreSQL in later sections
- **Claude Code** — install from [claude.ai/claude-code](https://claude.ai/claude-code) (required from Section 5 onwards)

## Quick Start

```bash
# Clone the repository
git clone https://github.com/serenity-dojo/cashback-rewards.git
cd cashback-rewards

# Check out the starting branch for your current section (see Branch Map below)
git checkout section-4/start

# Run the tests
./mvnw test

# Run unit + acceptance tests
./mvnw verify
```

If the tests pass, you're ready to go.

## Branch Map

Each course section has a **start** branch (where you begin working) and a **solution** branch (the completed code). Check out the start branch, follow along with the videos, and compare your work against the solution when you're done.

Sections 1–3 are theory and slides — no code branches needed. Hands-on coding starts at Section 4.

| Course Section | Branches | What's Introduced |
|---|---|---|
| **Section 4 — AI-Driven Requirements Discovery** | `section-4/start` · `section-4/solution` | The custom `/discover` command. Using Example Mapping with AI to write feature specifications. |
| **Section 5 — The CLAUDE.md File** | `section-5/start` · `section-5/solution` | The CLAUDE.md instruction file. Build commands, coding conventions, architecture rules, and the development process. |
| **Section 6 — Automating Architecture Rules** | `section-6/start` · `section-6/solution` | Path-scoped rules in `.claude/rules/`. Domain rules, persistence rules, test rules, and web rules. |
| **Section 7 — TDD with AI** | `section-7/start` · `section-7/solution` | The `/accept`, `/tdd`, and `/review` commands. Full TDD cycle: acceptance tests, inner-loop red-green-refactor, and code review. |
| **Section 8 — API Contracts** | `section-8/start` · `section-8/solution` | OpenAPI contract-driven development. Defining the API contract first, then implementing against it. |
| **Section 9 — Refactoring & Persistence** | `section-9/start` · `section-9/solution` | Refactoring from in-memory to PostgreSQL with Flyway migrations. Acceptance tests, persistence adapters, and JPA entities. |

### How to switch branches

```bash
# See all available branches
git branch -a

# Check out a starting point
git checkout section-7/start

# If you have local changes you want to keep
git stash
git checkout section-9/start
git stash pop
```

## Project Structure

The project follows **hexagonal architecture** (ports and adapters). Dependencies flow inward — adapters depend on the application layer, which depends on the domain. The domain never imports Spring or JPA.

```
src/main/java/com/serenitydojo/cashback_rewards/
├── domain/                          # Pure business logic — no frameworks
│   ├── model/                       # Value objects (Java records)
│   │   ├── Member.java              #   member id + local timezone
│   │   ├── Purchase.java            #   a purchase with the cashback it earned and when it posted
│   │   ├── CashbackEvent.java       #   one line of a cashback report
│   │   └── MonthlyCashbackReport.java
│   ├── service/                     # Domain services
│   │   ├── CashbackCalculator.java        # rate × amount, half-up to 2 decimals
│   │   ├── CategoryRateResolver.java      # merchant category code (MCC) -> rate
│   │   └── CashbackReportCalculator.java  # month selection in local timezone, ordering, total
│   └── exception/                   # Business rule exceptions
│
├── application/                     # Use-case orchestration
│   ├── port/in/                     # Inbound ports (RegisterMember, Purchase, CashbackReport use cases)
│   ├── port/out/                    # Outbound ports (save/find members and purchases)
│   └── service/                     # Application services (@Service beans)
│
├── adapter/                         # Framework-dependent code
│   ├── in/web/                      # REST controllers, request/response DTOs, ApiExceptionHandler
│   └── out/persistence/             # JPA entities, Spring Data repositories, persistence adapters
│
├── ClockConfiguration.java          # Clock bean, so time can be fixed in tests
└── DomainConfiguration.java         # Exposes the framework-free domain services as beans
```

### Test Structure

Tests mirror the production structure and follow Maven naming conventions:

```
src/test/java/com/serenitydojo/cashback_rewards/
├── acceptance/                      # *IT.java — end-to-end, run with mvn verify (failsafe)
│   ├── BasicCashbackCalculationIT.java
│   ├── MerchantCategoriesAndEligibilityAcceptanceIT.java
│   └── CashbackMonthlyReportAcceptanceIT.java
│
├── architecture/                    # ArchUnit rules: domain isolation and inward-only dependencies
├── domain/                          # Plain JUnit + AssertJ, no Spring
├── application/service/             # Unit tests for application services
├── adapter/in/web/                  # @WebMvcTest for controllers
└── adapter/out/persistence/         # @DataJpaTest for repositories
```

Run `./mvnw test` for unit and architecture tests only, or `./mvnw verify` for those plus the acceptance tests.

## Specifications

Feature specifications live in `doc/specs/` and follow the **Example Mapping** format: rules, examples, and counter-examples. These specs drive both the acceptance tests and the TDD implementation.

| Spec File | Feature |
|---|---|
| `earning-cashback.md` | Core user story for earning cashback on purchases |
| `basic-cashback-calculation.md` | Original cashback calculation rules (rounds down, per-merchant rates) |
| `basic-cashback-calculation-pending.md` | Revised calculation rules (rounds half-up, pending and available balances, refunds) |
| `merchant-categories-and-eligibility.md` | Category-based rates (MCC codes), eligibility rules, card status checks |
| `cashback-monthly-report.md` | Monthly cashback reporting |

**Where specs disagree, `merchant-categories-and-eligibility.md` wins.** It replaces per-merchant rates with rates derived from the merchant category code, so the "rate configured for the merchant" and "rate in force at the time of purchase" rules in the two basic-calculation specs no longer apply as written.

**Implemented so far:** the category rate rule, and the monthly report's month selection (in the member's local timezone), ordering and total. Not yet implemented: the eligibility rules (posted transactions, active cards, purchases only), pending and available balances, refunds as clawbacks, and the monthly cap.

## API

The REST contract lives in `doc/api/cashback-api.yaml` (OpenAPI 3.0). The service runs on port 8080.

| Endpoint | Purpose |
|---|---|
| `POST /members` | Register a member with their local timezone. `409` if the id already exists. |
| `POST /purchases` | Record a purchase and get the cashback it earned. The rate comes from the `mcc` field: Groceries 2%, Fuel 1%, anything else 0.5%. |
| `GET /members/{memberId}/cashback-reports/{month}` | Monthly cashback report for `YYYY-MM`, in the member's local timezone. |

Errors use `application/problem+json`: `400` for invalid input, `404` for an unknown member, `409` for a duplicate member. The contract still lists `capReached` and `forfeitedAmount` on the report, which the service does not return yet.

## Claude Code Setup

From Section 5 onwards, the project includes Claude Code configuration files that teach Claude how to work with this codebase.

### CLAUDE.md

The main instruction file at the project root. Contains build commands, coding conventions (BigDecimal for money, Java 25 features, constructor injection), the four-step development process (Discover → Accept → TDD → Review), testing standards, and architecture rules.

### Rules (`.claude/rules/`)

Path-scoped rules that activate automatically when Claude edits files in specific directories:

| Rule File | Scope | Key Constraints |
|---|---|---|
| `domain-rules.md` | `src/**/domain/**` | No Spring imports, no JPA, pure Java only |
| `persistence-rules.md` | `src/**/adapter/out/persistence/**` | JPA entities here only, implement outbound ports |
| `test-rules.md` | `src/test/**` | Naming conventions (*Test vs *IT), never recalculate expected values |
| `web-rules.md` | `src/**/adapter/in/web/**` | Thin controllers, DTOs only, @Valid on request bodies |

### Commands (`.claude/commands/`)

Reusable prompts for the spec-driven development workflow:

| Command | Model | Purpose |
|---|---|---|
| `/discover` | opus | Run Example Mapping to discover rules, examples, and questions from a user story |
| `/accept` | sonnet | Write a failing acceptance test for the next spec rule |
| `/tdd` | sonnet | Run one RED → GREEN → REFACTOR TDD cycle |
| `/review` | opus | Architecture and code quality review of uncommitted changes |

### Hooks (`.claude/settings.json`)

The project includes a PostToolUse hook that automatically runs `mvn verify -q` (unit, architecture and acceptance tests) after every file edit, ensuring Claude never moves forward with broken code.

## Architecture Decisions

**Money handling** — All monetary values use `BigDecimal` with explicit `RoundingMode.HALF_UP` and scale 2. Never `float`, `double`, or `int` for money.

**Hexagonal architecture** — Domain code is framework-free. Spring and JPA live only in adapters. This makes the domain testable with plain JUnit — no Spring context needed.

**Real persistence in every test that needs it** — Acceptance tests run the full stack (controller, service, domain, JPA) against Spring Boot's default in-memory H2 database, with no mocks. Repository tests use `@DataJpaTest`. Application services are unit tested with their outbound ports stubbed as lambdas, which is why each outbound port is a small single-purpose interface.

**Architecture is enforced, not just documented** — `HexagonalArchitectureTest` (ArchUnit) fails the build if the domain imports Spring or JPA, or if a dependency points outward.

**Time comes from a `Clock`** — Services that need "now" take a `Clock`, so tests can fix the time.

**Rates come from the merchant category code** — `CategoryRateResolver` maps an MCC to a rate. Merchants are not registered and carry no rate of their own.

**Specs as the source of truth** — Feature specifications in `doc/specs/` define the contract. Acceptance tests verify the contract. Production code implements it. When specs change, tests change first.

## Useful Commands

```bash
./mvnw test                                    # Unit and architecture tests only
./mvnw verify                                  # Unit, architecture and acceptance tests
./mvnw -Dtest=CashbackReportCalculatorTest test # Single test class
./mvnw spring-boot:run                         # Run the app on port 8080 (in-memory H2, no setup needed)
./mvnw clean package                           # Build the JAR
```

### With Claude Code

```bash
claude                                   # Start a Claude Code session
/discover "As a customer, I want..."     # Run Example Mapping on a user story
/accept Rule1 @doc/specs/feature.md      # Write an acceptance test for a spec rule
/tdd ClassName#methodName                # Run one TDD cycle
/review                                  # Review uncommitted changes
```

---

## About the Course

**[Spec-Driven Development and TDD with AI](https://www.udemy.com/course/spring-boot-ai-tdd/?referralCode=3170E302C61D48703A94)** teaches you to build production-ready Spring Boot APIs using Claude Code as your AI pair programmer. You'll learn a complete workflow: discover requirements with Example Mapping, write specifications, generate tests, and build features using TDD — all with AI assistance that's configured to follow your project's architecture and conventions.

The course is designed for Java developers who want to use AI effectively — not as a code generator, but as a disciplined development partner.

**[Enrol on Udemy →](https://www.udemy.com/course/spring-boot-ai-tdd/?referralCode=3170E302C61D48703A94)** · Built by [Serenity Dojo](https://www.serenity-dojo.com/)
