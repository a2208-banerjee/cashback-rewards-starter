# CLAUDE.md

## Project

SpringBoot microservice implementing a Cashback Rewards solution.

## Build & Run

Uses the Maven wrapper; Java 25 required (see `pom.xml` `<java.version>`).

```bash
./mvnw spring-boot:run           # run the app
./mvnw test                      # unit and architecture tests only
./mvnw verify                    # unit, architecture and acceptance (*IT) tests
./mvnw -Dtest=ClassName test     # run a single test class
./mvnw -Dtest=ClassName#method test   # run a single test method
./mvnw clean package             # build the jar
```

`./mvnw verify` is the full check. Acceptance tests are named `*IT` and only run under `verify`.
Every API change must keep `doc/api/cashback-api.yaml` in step. Lint it with
`npx @redocly/cli lint doc/api/cashback-api.yaml`.

Stack: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, H2 (runtime). No explicit datasource config — Spring Boot auto-configures an in-memory H2 via `application.yaml`.

## Coding Conventions

### Money
BigDecimal for ALL monetary values. NEVER float, double, or int.
Always explicit RoundingMode. Cashback: RoundingMode.HALF_UP, scale 2.
BigDecimal.valueOf() or new BigDecimal("...") — NEVER new BigDecimal(double).
Cashback rates are fractions: 0.02 means 2%. Never store or send a percentage figure.
Money columns have explicit precision and scale (19,2).

### Time
Store and pass instants as `Instant`. Decide which calendar month an event belongs to by converting to the
member's `ZoneId` (never a fixed offset, never UTC) — DST changes the answer.
Never call `Instant.now()` or `LocalDate.now()` in production code. Inject a `Clock`; tests fix it.

### Java 25
Records for value objects, sealed interfaces, pattern matching.
No Lombok — records replace it.

### REST & Spring
Constructor injection only (no field @Autowired).
@Valid on request bodies. 201 create, 200 query, 400 validation, 404 not found, 409 duplicate.
Domain exceptions for business rule violations. Map to HTTP in the web adapter only (`ApiExceptionHandler`),
never in domain or application. Exception-to-status mapping lives in that @RestControllerAdvice, not in each
controller.
Never swallow exceptions or leak infrastructure details.

Request DTOs are records with Bean Validation constraints (@NotBlank, @NotNull). Let Jackson parse typed
fields (ZoneId, Instant) so a bad value is rejected as an unreadable request, not parsed by hand.
All errors are `application/problem+json`, produced by `ApiExceptionHandler` (a @RestControllerAdvice that
extends ResponseEntityExceptionHandler). A new domain exception means one new handler method there.
`detail` text comes only from domain exception messages. Never include exception class names or framework
messages.
Status mapping: 400 invalid input, 404 unknown member, 409 duplicate member.

## Project Structure

domain/ — business logic and models. No Spring imports. Ports live in application/port/.
application/ — use-case orchestration.
adapter/in/web/ — REST controllers (Spring MVC).
adapter/out/persistence/ — JPA repositories and entities.

NEVER import adapter classes from domain.

## Development Process - ATDD + TDD

Follow these steps for every feature. Do NOT skip steps.

Step 1: Discovery — Run /discover.
Propose rules, surface questions with options, let the user decide.
Save draft spec to doc/specs/.
STOP. User reviews, edits, and annotates the spec.
Do NOT proceed if the spec has unresolved questions.
Re-read the final spec before continuing.
When a new spec replaces rules in an older one, say so in the new spec's Notes, and update or retire the old
specs and their tests in the same change.

Step 2: Acceptance Test — Write test for the NEXT rule only.
@Nested = rule, test = example. @SpringBootTest + MockMvc.
Complete Step 3 until this rule is GREEN before writing the next.

Step 3: TDD (Inner Loop) — RED → GREEN → REFACTOR.
Write ONE failing test. Minimum code to pass. Refactor.
Run ALL tests. STOP after each cycle.

Step 4: Review — Verify coverage, boundaries, no AI smells.
Update CLAUDE.md if new conventions emerged.

## Testing Standards

Acceptance tests live in .../acceptance/, unit tests beside their production code.
Domain tests: plain JUnit + AssertJ, NO Spring.
Repository tests: @DataJpaTest.
Web tests: @WebMvcTest.
Acceptance tests: @SpringBootTest + MockMvc.
For money: isEqualByComparingTo("1.60").
Inline test data per test. No shared fixtures.

Tests are executable specifications, not just checklists. Each test must assert the business outcome, not just the happy path.
Use @Nested classes to group tests by rule. Even in unit tests. Each test method must cover a distinct example or counter-example.
@DisplayName for all tests. Use descriptive names, not "test1", "test2", etc.
Use AssertJ for assertions. No JUnit assertions. 
Use isEqualByComparingTo() for BigDecimal comparisons.
Inline test data per test. No shared fixtures.

Spec tables become a @ParameterizedTest with @CsvSource, one row per table row.
Every 400/404/409 web test also asserts `verifyNoInteractions(useCase)` so bad input never reaches the use case.
Acceptance tests use real H2 and no mocks. Use unique ids per test, because the context and database are
shared within a run.
ArchUnit rules are plain @Test methods that call `.check(...)`. `@ArchTest` on a no-argument method inside a
@Nested class is silently never run.
If a new test passes immediately, mutation-check it: break the production code, confirm the test fails, restore.
A signature change that ripples into existing tests may only change calls and constructors, never expected values.
Spring Boot 4: MockMvc in `@SpringBootTest` needs `spring-boot-starter-webmvc-test`
(`org.springframework.boot.webmvc.test.autoconfigure`); `@DataJpaTest` needs `spring-boot-starter-data-jpa-test`
(`org.springframework.boot.data.jpa.test.autoconfigure`).

## Architecture: Hexagonal (Ports & Adapters)
Domain (domain/): Pure Java. NO Spring, NO framework dependencies.
    model/ — entities and value objects
    service/ — business rules
Application (application/): port/in/ and port/out/ interfaces.
    @Service orchestration only — no business logic here.
Adapters:
    adapter/in/web/ — @RestController, DTOs only.
    adapter/out/persistence/ — JPA repos and entities (NOT in domain).

Domain NEVER imports org.springframework or jakarta.persistence.
Controllers NEVER contain business logic.
Dependencies flow inward: adapter → application → domain.

Outbound ports are one small interface per capability (SaveMemberPort, FindMemberPort,
FindCustomerPurchasesPort), so application tests can stub them with lambdas. An adapter may implement several.
Domain services are plain classes with no Spring annotations. Expose them as beans in the root
`DomainConfiguration`. Application services are @Service with constructor injection.
JPA entities are package-private and never leave the persistence package. An adapter translates persistence
failures into domain exceptions (for example DataIntegrityViolationException -> MemberAlreadyExistsException).
Create-only entities implement `Persistable` and report themselves as new, so `save` inserts and the primary key
rejects duplicates instead of Spring Data silently overwriting. Use `saveAndFlush` where the adapter must catch
the violation.
