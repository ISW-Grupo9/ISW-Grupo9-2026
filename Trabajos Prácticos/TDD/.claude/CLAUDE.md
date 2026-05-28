# Claude Instructions — TDD TP EcoHarmony Park

## Project context
TP6 - Práctico Evaluable: TDD (Test-Driven Development)
User Story: Comprar Entradas (US #8) — grupo impar
Stack: Java 21 + Spring Boot 3 (backend) / React 19 + TypeScript + Vite (frontend)

## Always use TDD
See `.claude/skills/test-driven-development.md`.
Never write production code without a failing test first.
Reference the planned cycles in `docs/ciclos-tdd.md` to know what to test next.

## Design decisions and open PO questions
See `docs/decisiones-de-diseno.md` before implementing any business rule.
If a PO question is still open (⏳), use the documented assumption.

## Cycle order
Implement backend first (Ciclos 1–13), then frontend (Ciclos 14–21).
Within each layer, start from the most granular (model/utils) up to integration.

## No over-engineering
Implement exactly what the current test requires.
Do not add features, abstractions, or generalization beyond the failing test.
