---
date: 2026-03-26
topic: scientific-calculator-app
---

# Scientific Calculator Android App (Ad-Supported)

## Problem Frame

The top scientific calculator apps on Google Play (HiPER: 40M+ downloads, RealCalc: 20M+ downloads) have dated UIs from ~2015 with marbly backgrounds, serif fonts, and cramped buttons. Users consistently complain about ugly design, intrusive ad placement (pop-ups on the equals button), preferences not persisting, and small touch targets. There is a clear opening for a modern, Material You scientific calculator that competes on design quality and respectful ad integration while matching feature parity.

**Target users:** Students (high school, university), engineers, and professionals who need scientific calculator functions daily on Android.

**Revenue model:** Ad-supported (Google AdMob) with optional premium ad-removal IAP.

## Market Research

### Competitors
| App | Downloads | Rating | Weakness |
|---|---|---|---|
| HiPER Scientific Calculator | 40M+ | 4.7 | Dated UI, cluttered layout |
| RealCalc Scientific Calculator | 20M+ | 4.6 | Old design, no Material You |
| Scientific Calculator Plus 991 | 10M+ | 4.5 | Confusing notation, ad-heavy |
| Google Calculator | Pre-installed | 4.3 | No scientific functions beyond basics |

### User Pain Points (from Play Store reviews)
1. Ugly, outdated design (marbly backgrounds, serif fonts)
2. Intrusive ads (pop-ups when pressing equals)
3. Dark mode and preferences reset on each launch
4. Buttons too small, no landscape mode
5. Missing log base n, notation toggle (fraction/decimal/scientific)
6. Confusing notation system (infix vs postfix for trig)

### Ad Revenue Estimates (AdMob, Tier 1 countries)
- Banner ads: ~$0.50-1.30 eCPM
- Interstitial ads: $5-8 eCPM
- Rewarded video ads: $15-30 eCPM
- Calculator apps benefit from high daily usage frequency = consistent impressions

## Requirements

### Core Calculator
- R1. Standard arithmetic operations (+, -, x, /, %, parentheses)
- R2. Full scientific functions: trigonometric (sin, cos, tan + inverses + hyperbolic), logarithmic (ln, log, log base n), exponential, factorial, powers, roots, absolute value
- R3. Constants: pi, e, and common physical/mathematical constants
- R4. Calculation history with scroll-back, tap-to-reuse, and swipe-to-delete
- R5. Multiple display formats: decimal, fraction, scientific notation with user toggle
- R6. Permutations, combinations, modulo operations
- R7. Expression input with natural textbook-style display (e.g., fractions shown as stacked, exponents as superscript)

### Graphing
- R8. 2D function graphing: plot y=f(x) with pinch-to-zoom and pan
- R9. Multiple simultaneous graphs with color coding
- R10. Trace mode: tap graph to see (x, y) coordinates at any point
- R11. Support for common function types: polynomial, trigonometric, exponential, logarithmic

### Equation Solver
- R12. Linear equation solver (single variable)
- R13. Quadratic equation solver with step-by-step solution display
- R14. System of linear equations (2x2, 3x3)

### Unit Converter
- R15. Built-in unit converter with categories: length, weight, temperature, volume, area, speed, time, data storage, pressure, energy
- R16. Minimum 100+ units across all categories
- R17. Offline-capable (no network required for conversions)

### Design & UX
- R18. Material 3 / Material You design with dynamic color theming (Android 12+)
- R19. Dark mode and light mode with persistent preference
- R20. Landscape mode with expanded button layout
- R21. Large, well-spaced touch targets (minimum 48dp)
- R22. Haptic feedback on button press
- R23. Smooth animations for mode switching and panel transitions

### Monetization
- R24. Banner ad at bottom of calculator screen (non-intrusive, does NOT overlap buttons)
- R25. Interstitial ad after every 5th calculation session (not per calculation)
- R26. Rewarded video ad: "Watch ad to unlock calculation history export" or similar value exchange
- R27. Premium IAP: one-time purchase to remove all ads
- R28. No ads during active input or when pressing equals (key differentiator vs competitors)

### Technical
- R29. Kotlin + Jetpack Compose (native Android)
- R30. Minimum SDK: API 24 (Android 7.0) for broad reach
- R31. App size under 15MB
- R32. Offline-first: all core features work without internet (ads degrade gracefully)
- R33. Google AdMob SDK for ad mediation

## Success Criteria

- 10,000+ downloads within first 3 months (organic + ASO)
- 4.5+ star rating on Play Store
- Average daily session time > 2 minutes (drives ad impressions)
- Ad revenue covering Google Play developer fee ($25) within first month
- User retention: 30-day retention > 20%

## Scope Boundaries

- No iOS version in v1 (Android only, Kotlin/Compose)
- No CAS (computer algebra system) / symbolic math
- No matrix/vector operations in v1
- No programming mode (binary/octal/hex) in v1
- No cloud sync or account system
- No 3D graphing

## Key Decisions

- **Kotlin + Jetpack Compose over Flutter**: Native Android gives best Material You integration, dynamic colors, and AdMob SDK compatibility. User's strongest skill set.
- **Full feature set in v1 over phased release**: User prefers to launch with complete feature set (graphing + equation solver + unit converter) despite longer build time (~3-4 weeks).
- **Material You as primary differentiator**: Competitors have dated UIs. Modern design is the main competitive advantage.
- **Respectful ad placement**: No ads during active calculation. This directly addresses the #1 user complaint about competitors.

## Dependencies / Assumptions

- Google AdMob account setup required before launch
- Google Play Developer account ($25 one-time fee)
- Expression parsing library or custom parser needed for natural display (R7)
- Graphing library or custom Canvas/Compose drawing for R8-R11
- AdMob SDK compatible with target min SDK (API 24)

## Outstanding Questions

### Resolve Before Planning
- None (all product decisions resolved)

### Deferred to Planning
- [Affects R7][Needs research] Which expression parsing approach: custom parser vs existing library (e.g., exp4j, mXparser)?
- [Affects R8-R11][Needs research] Custom Compose Canvas graphing vs existing charting library (e.g., MPAndroidChart, Vico)?
- [Affects R14][Technical] Gaussian elimination implementation vs library for system of equations solver?
- [Affects R24-R26][Technical] AdMob mediation setup: single network vs multi-network mediation for better fill rates?
- [Affects ASO][Needs research] Optimal app name and keyword strategy for Play Store ranking

## Next Steps

-> `/ce:plan` for structured implementation planning
