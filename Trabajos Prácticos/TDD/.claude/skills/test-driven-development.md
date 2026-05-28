# Skill: Test-Driven Development

Use this skill when implementing any feature or fix in this project.
Write the test first, watch it fail, write minimal code to pass.

## The Iron Law
NO PRODUCTION CODE WITHOUT A FAILING TEST FIRST.
If you wrote code before the test → delete it, start over.

## Red-Green-Refactor

### RED — Write failing test
- One behavior per test
- Clear name that describes the behavior
- Use real code, not mocks, unless unavoidable
- Run the test and confirm it FAILS for the expected reason

### GREEN — Minimal code
- Write the simplest code that makes the test pass
- No extras, no "while I'm here" features
- Run the test and confirm it PASSES

### REFACTOR — Clean up
- Remove duplication
- Improve names
- Extract helpers only if they appear 3+ times
- All tests must stay green after refactor

## Project-specific test commands

### Backend (Spring Boot + Java 21)
```bash
# Run a specific test class
./mvnw test -Dtest=CompraServiceTest

# Run all tests
./mvnw test

# Run a specific method
./mvnw test -Dtest=CompraServiceTest#debe_calcular_total_mix_regular_y_vip
```

### Frontend (Vitest + RTL)
```bash
# Run a specific test file
npx vitest run src/__tests__/useCompraForm.test.ts

# Run all tests
npx vitest run

# Watch mode
npx vitest
```

## Cycle reference for this project
See: `docs/ciclos-tdd.md` for all 21 planned cycles.

## Stack
- Backend: Java 21, Spring Boot 3, JUnit 5, Mockito, AssertJ, MockMvc
- Frontend: React 19, TypeScript, Vitest, React Testing Library, MSW v2

## Anti-patterns to avoid
- Testing mock behavior instead of real behavior
- Adding test-only methods to production classes
- Mocking without understanding the dependency
- Writing tests after implementation
- Saying "I'll test this later"

## Verification checklist before marking any cycle complete
- [ ] Every new method has a test
- [ ] Watched each test fail before implementing
- [ ] Test failed for the expected reason (missing feature, not a typo)
- [ ] Wrote minimal code to pass
- [ ] All tests pass
- [ ] No warnings or errors in output
- [ ] Edge cases covered (see ciclos-tdd.md for the full list)
