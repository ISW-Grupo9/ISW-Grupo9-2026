# Skill: frontend-design
Stack: React 19 + TypeScript + Vite + Tailwind CSS v4

## When to use
When asked to build, style, beautify, or redesign any frontend component, page, or UI in this project.

## Design Thinking Protocol
Before coding, commit to a clear aesthetic direction:

1. **Purpose**: What does this UI solve? Who uses it?
2. **Tone**: Pick an extreme and commit — refined minimal, organic/natural, editorial, brutalist, etc.
3. **Differentiation**: What single thing makes it memorable?
4. **Constraint check**: Keep all `data-testid`, `aria-label`, `role` attrs intact — tests depend on them.

Never produce generic "AI slop": avoid Inter/Roboto, purple gradients on white, predictable layouts.

---

## Tech Stack Details

### Tailwind v4 (`@import "tailwindcss"` in `index.css`)
- Use `@theme {}` to register custom tokens — they become utility classes automatically:
  ```css
  @theme {
    --color-forest-900: #1B4332;   /* → bg-forest-900, text-forest-900, border-forest-900 */
    --color-cream-100: #F8F3E8;    /* → bg-cream-100 */
    --font-display: 'Font Name', serif;  /* → font-display */
    --font-sans: 'Font Name', sans-serif; /* → font-sans (overrides default) */
  }
  ```
- Import Google Fonts via `@import url(...)` **before** `@import "tailwindcss"` in CSS
- Arbitrary values still work: `bg-[#1B4332]`, `text-[#C9902A]`
- No config file needed — `@theme` is the config in v4

### Component Rules
- Keep all logic, hooks, props, and types **unchanged** — style-only edits
- Keep `data-testid="..."` attributes — test suite depends on them
- Keep `aria-label`, `role="alert"`, `aria-pressed` attributes
- Prefer Tailwind utilities; add minimal custom CSS only when utilities can't express the design

### Animation
- Use Tailwind's `transition-*`, `duration-*`, `ease-*` classes for micro-interactions
- CSS `@keyframes` in `index.css` for entrance animations
- No external animation libraries (project has none installed)

---

## Aesthetic Guidelines

### Typography
- Pair a **distinctive display/serif** for headings with a **clean humanist sans** for body
- Good pairs: Cormorant Garamond + DM Sans, Playfair Display + Inter, Libre Baskerville + Nunito
- Apply with `font-display` (heading) and `font-sans` (body) after defining in `@theme`
- Size scale: hero titles 3xl–4xl, section headers xl–2xl, labels xs uppercase tracking-widest

### Color
- Commit to a dominant palette (2–3 hues) + 1 accent
- Define all in `@theme` as `--color-{name}-{shade}` for utility access
- Avoid evenly-distributed palettes — dominant + accent beats rainbow

### Spacing & Layout
- Cards: `rounded-2xl`, `shadow-sm`, `border`, generous `p-6` inner padding
- Page: `max-w-2xl mx-auto px-4 py-8` with `space-y-5` between sections
- Form inputs: `px-4 py-3 rounded-xl bg-cream-50 border border-cream-300` pattern

### Inputs & Controls
- All inputs: visible label above (xs uppercase tracking-widest), focus ring (`focus:ring-2 focus:ring-forest-800/20`), error border in red
- Toggle buttons (payment): outlined style default, filled when selected (`border-2 bg-forest-900 text-white`)
- CTA button: full width, brand color, generous padding (`py-4`), `w-full`

---

## EcoHarmony Park Aesthetic Reference
The project's established look (eco-luxury / refined nature):
- Forest green family: `#1B4332` (forest-900), `#2D6A4F` (forest-800), `#52B788` (forest-600)
- Cream family: `#F8F3E8` (cream-100), `#EDE8D5` (cream-200), `#DDD5BE` (cream-300)
- Accent gold: `#C9902A` (gold-500)
- Display font: Cormorant Garamond (serif, headers)
- Body font: DM Sans (sans-serif, body text)
- Header pattern: dark forest background + display font park name
- Cards: white bg, `border border-cream-200 shadow-sm rounded-2xl`
- Summary card: inverted — `bg-forest-900 text-white`
