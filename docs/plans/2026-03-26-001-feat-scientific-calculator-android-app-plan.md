---
title: "feat: Scientific Calculator Android App (Ad-Supported)"
type: feat
status: active
date: 2026-03-26
origin: docs/brainstorms/2026-03-26-scientific-calculator-app-requirements.md
---

# feat: Scientific Calculator Android App (Ad-Supported)

## Overview

Build a modern, Material You scientific calculator for Android using Kotlin + Jetpack Compose. The app competes against dated incumbents (HiPER 40M+, RealCalc 20M+) by offering a clean, modern UI with respectful ad placement. Full feature set in v1: scientific calculator, graphing, equation solver, and unit converter. Monetized via AdMob (banner + interstitial + rewarded) with optional premium ad-removal IAP.

## Problem Statement / Motivation

Top scientific calculator apps on Google Play have UIs from ~2015. Users consistently complain about ugly design, intrusive ads (pop-ups on equals), preferences not persisting, and cramped buttons. There is a clear market gap for a Material 3 calculator that solves these exact pain points while matching feature parity. (see origin: `docs/brainstorms/2026-03-26-scientific-calculator-app-requirements.md`)

## Proposed Solution

A single-module Android app built with Kotlin + Jetpack Compose following MVVM architecture. The app has four main functional areas accessible via bottom navigation or tab switching:

1. **Calculator** — Scientific calculator with natural textbook display
2. **Graphing** — 2D function plotter with pinch-to-zoom
3. **Solver** — Linear, quadratic, and system-of-equations solver
4. **Converter** — Unit converter with 100+ units across 10 categories

## Technical Approach

### Architecture

```
app/
├── src/main/java/com/calcmate/
│   ├── CalcMateApp.kt                    # Application class
│   ├── MainActivity.kt                   # Single Activity, hosts Compose + AdMob
│   │
│   ├── core/
│   │   ├── parser/
│   │   │   ├── Token.kt                  # Token types (NUMBER, PLUS, SIN, etc.)
│   │   │   ├── Lexer.kt                  # Tokenizer: string -> tokens
│   │   │   ├── Parser.kt                 # Recursive descent parser -> AST
│   │   │   ├── ASTNode.kt               # Expression tree nodes
│   │   │   ├── Evaluator.kt             # AST -> numeric result
│   │   │   └── Formatter.kt             # Result -> display format (decimal/fraction/sci)
│   │   ├── model/
│   │   │   ├── CalculatorState.kt        # UI state for calculator screen
│   │   │   ├── GraphState.kt             # UI state for graphing screen
│   │   │   ├── SolverState.kt            # UI state for solver screen
│   │   │   ├── ConverterState.kt         # UI state for converter screen
│   │   │   ├── CalculatorAction.kt       # Sealed class: all calculator input actions
│   │   │   └── HistoryEntry.kt           # Calculation history data class
│   │   ├── math/
│   │   │   ├── Constants.kt              # Pi, e, physical constants
│   │   │   ├── Combinatorics.kt          # nPr, nCr, factorial
│   │   │   └── EquationSolver.kt         # Linear, quadratic, Gaussian elimination
│   │   └── data/
│   │       ├── HistoryRepository.kt      # Room-backed history persistence
│   │       ├── HistoryDao.kt             # Room DAO
│   │       ├── AppDatabase.kt            # Room database
│   │       ├── PreferencesManager.kt     # DataStore for user prefs (theme, format)
│   │       └── UnitData.kt              # Static unit conversion data
│   │
│   ├── feature/
│   │   ├── calculator/
│   │   │   ├── CalculatorViewModel.kt
│   │   │   ├── CalculatorScreen.kt       # Main calculator UI
│   │   │   ├── ScientificKeypad.kt       # Button grid (portrait)
│   │   │   ├── ScientificKeypadLand.kt   # Button grid (landscape)
│   │   │   ├── ExpressionDisplay.kt      # Natural textbook-style rendering
│   │   │   └── HistorySheet.kt           # Bottom sheet with history
│   │   ├── graphing/
│   │   │   ├── GraphViewModel.kt
│   │   │   ├── GraphScreen.kt
│   │   │   ├── GraphCanvas.kt            # Custom Canvas for function plotting
│   │   │   ├── GraphControls.kt          # Function input, color picker
│   │   │   └── TraceOverlay.kt           # Tap-to-trace coordinates
│   │   ├── solver/
│   │   │   ├── SolverViewModel.kt
│   │   │   ├── SolverScreen.kt
│   │   │   ├── LinearSolverPanel.kt
│   │   │   ├── QuadraticSolverPanel.kt
│   │   │   └── SystemSolverPanel.kt
│   │   └── converter/
│   │       ├── ConverterViewModel.kt
│   │       ├── ConverterScreen.kt
│   │       ├── CategorySelector.kt
│   │       └── UnitPicker.kt
│   │
│   ├── ads/
│   │   ├── AdManager.kt                  # AdMob lifecycle, load/show logic
│   │   ├── BannerAdComposable.kt         # AndroidView wrapper for banner
│   │   ├── InterstitialAdHelper.kt       # Session counter + interstitial trigger
│   │   └── RewardedAdHelper.kt           # Rewarded video for history export
│   │
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── Theme.kt                  # Material You dynamic color theme
│   │   │   ├── Color.kt                  # Fallback colors for API < 31
│   │   │   └── Type.kt                   # Typography
│   │   ├── components/
│   │   │   ├── CalcButton.kt             # Reusable calculator button (48dp+, haptic)
│   │   │   ├── NavigationBar.kt          # Bottom nav: Calc, Graph, Solver, Converter
│   │   │   └── SettingsDialog.kt         # Theme, format, about
│   │   └── navigation/
│   │       └── AppNavigation.kt          # NavHost + routes
│   │
│   └── util/
│       └── HapticFeedback.kt             # Vibration helper
│
├── src/main/res/
│   ├── values/themes.xml                 # Material 3 theme base
│   └── values-night/themes.xml           # Dark theme
│
├── src/test/                             # Unit tests
│   ├── parser/LexerTest.kt
│   ├── parser/ParserTest.kt
│   ├── parser/EvaluatorTest.kt
│   ├── math/EquationSolverTest.kt
│   └── math/CombinatoricsTest.kt
│
├── src/androidTest/                      # Instrumented tests
│   ├── CalculatorScreenTest.kt
│   └── GraphCanvasTest.kt
│
└── build.gradle.kts
```

### Key Technical Decisions

| Decision | Choice | Rationale |
|---|---|---|
| Expression parser | Custom recursive descent | mXparser requires paid commercial license for ad-supported apps. exp4j is unmaintained. Custom parser gives full control for natural display (R7) and zero licensing risk. |
| Graphing engine | Custom Compose Canvas | Vico/MPAndroidChart are for data charts, not y=f(x) math plotting. Canvas `drawPath` with expression evaluation at sample points. Full control over zoom, pan, trace. |
| Equation solver | Direct implementation | Quadratic formula + Gaussian elimination for 2x2/3x3 are simple algorithms. No library overhead. |
| AdMob integration | Next Gen SDK + AndroidView | Banner via `AndroidView` composable wrapper. Interstitial/rewarded loaded at Activity level. MVVM ad state management. |
| Persistence | Room + DataStore | Room for calculation history (queryable, swipe-to-delete). DataStore for preferences (theme, display format). |
| Architecture | Single-module MVVM | ViewModel per feature screen. Sealed action classes for input events. Single module is sufficient for v1 scope. |
| Min SDK | API 24 (Android 7.0) | Broad device coverage (~99% of active devices). Material You dynamic colors degrade gracefully on API < 31. |

### Implementation Phases

#### Phase 1: Project Setup + Core Calculator Engine (~5 days)

**Goal:** Working calculator that can parse and evaluate scientific expressions.

**Tasks:**
- [ ] Create new Android project: `com.calcmate.scientificcalculator`
  - Kotlin, Compose, API 24 min, Material 3
  - `build.gradle.kts` with dependencies: Compose BOM, Material3, Navigation, Room, DataStore, Hilt
- [ ] Implement expression parser (`core/parser/`)
  - `Token.kt` — define token types: NUMBER, PLUS, MINUS, MULTIPLY, DIVIDE, POWER, LPAREN, RPAREN, SIN, COS, TAN, ASIN, ACOS, ATAN, SINH, COSH, TANH, LN, LOG, LOG_BASE, SQRT, CBRT, ABS, FACTORIAL, PI, E, MOD, NPR, NCR
  - `Lexer.kt` — tokenize input string, handle implicit multiplication (e.g., `2pi` -> `2*pi`)
  - `Parser.kt` — recursive descent: expression -> term -> factor -> unary -> primary. Operator precedence: `+/-` < `*/÷/%` < `^` < unary functions < factorial
  - `ASTNode.kt` — sealed class hierarchy: NumberNode, BinaryOpNode, UnaryFuncNode, ConstantNode
  - `Evaluator.kt` — tree-walk evaluator, handle: division by zero (return Infinity/NaN), overflow (BigDecimal fallback for factorial), domain errors (log(-1) -> NaN)
  - `Formatter.kt` — format result as decimal, fraction (using GCD), or scientific notation based on user preference
- [ ] Implement math utilities (`core/math/`)
  - `Constants.kt` — pi, e, speed of light, Avogadro, Planck, Boltzmann, etc.
  - `Combinatorics.kt` — factorial (iterative, BigInteger for large n), nPr, nCr
- [ ] Unit tests for parser pipeline
  - `LexerTest.kt` — tokenization edge cases (negative numbers, implicit multiplication, function names)
  - `ParserTest.kt` — AST construction for nested expressions
  - `EvaluatorTest.kt` — arithmetic accuracy, trig functions (degree/radian), log base n, edge cases (0!, division by zero, very large numbers)

**Acceptance Criteria:**
- [ ] Parser correctly handles all R1, R2, R3, R5, R6 operations
- [ ] All unit tests pass with >95% coverage on parser module

---

#### Phase 2: Calculator UI (~4 days)

**Goal:** Beautiful, functional calculator screen with Material You design.

**Tasks:**
- [ ] Set up Material You theming (`ui/theme/`)
  - `Theme.kt` — `dynamicColorScheme` for API 31+, fallback palette for older devices
  - `Color.kt` — primary/secondary/tertiary colors, surface tones
  - `Type.kt` — display font for results (monospace or tabular figures), body font for buttons
- [ ] Build reusable components (`ui/components/`)
  - `CalcButton.kt` — minimum 48dp touch target, ripple effect, haptic feedback on press, configurable colors for number/operator/function/equals
  - `NavigationBar.kt` — bottom navigation with 4 destinations (Calculator, Graph, Solver, Converter)
- [ ] Build calculator screen (`feature/calculator/`)
  - `ExpressionDisplay.kt` — scrollable expression input area at top. Natural textbook-style rendering: stacked fractions, superscript exponents, sqrt symbol with vinculum. Use Compose `Canvas` + `Text` composables for layout.
  - `ScientificKeypad.kt` (portrait) — 5-6 rows of buttons. Row 1: scientific functions (sin, cos, tan, ln, log). Row 2: inverse/hyp toggle + more functions (sqrt, ^, !, π, e). Row 3-5: number pad + operators. Row 6: equals, decimal, backspace. Toggle button to switch between basic and scientific modes.
  - `ScientificKeypadLand.kt` (landscape) — expanded layout showing all buttons without toggle, wider display area
  - `HistorySheet.kt` — bottom sheet showing recent calculations, tap to reuse expression or result, swipe to delete
  - `CalculatorScreen.kt` — compose display + keypad + history access, handle portrait/landscape
- [ ] Implement `CalculatorViewModel.kt`
  - State: expression string, result, display format, history list, isScientificMode
  - Actions sealed class: Digit, Operator, Function, Constant, Equals, Clear, Backspace, ToggleFormat, ToggleMode
  - On Equals: parse -> evaluate -> format -> add to history
- [ ] Implement preferences (`core/data/PreferencesManager.kt`)
  - DataStore for: theme mode (system/light/dark), display format (decimal/fraction/scientific), angle unit (degree/radian), haptic enabled
  - Persist across app restarts (addresses competitor pain point #3)
- [ ] Set up navigation (`ui/navigation/AppNavigation.kt`)
  - NavHost with 4 routes, smooth crossfade transitions

**Acceptance Criteria:**
- [ ] Calculator UI matches Material 3 guidelines with dynamic colors on Android 12+
- [ ] All buttons >= 48dp touch target (R21)
- [ ] Landscape mode shows expanded keypad (R20)
- [ ] Dark mode persists across restarts (R19)
- [ ] Natural display renders fractions and exponents (R7)

---

#### Phase 3: History + Persistence (~2 days)

**Goal:** Calculation history persists across app restarts.

**Tasks:**
- [ ] Set up Room database (`core/data/`)
  - `HistoryEntry.kt` — data class: id, expression, result, displayFormat, timestamp
  - `HistoryDao.kt` — insert, getAll (ordered by timestamp desc), delete, deleteAll
  - `AppDatabase.kt` — Room database with HistoryEntry entity
  - `HistoryRepository.kt` — wraps DAO, exposes Flow<List<HistoryEntry>>
- [ ] Wire history into CalculatorViewModel
  - On successful evaluation: save to Room
  - Expose history as StateFlow for HistorySheet
  - Tap-to-reuse: load expression back into input
  - Swipe-to-delete: remove single entry
  - Clear all history option

**Acceptance Criteria:**
- [ ] History survives app restart and process death
- [ ] R4: scroll-back, tap-to-reuse, swipe-to-delete all work

---

#### Phase 4: Unit Converter (~3 days)

**Goal:** Full unit converter with 100+ units across 10 categories.

**Tasks:**
- [ ] Define unit data (`core/data/UnitData.kt`)
  - Categories: Length, Weight/Mass, Temperature, Volume, Area, Speed, Time, Data Storage, Pressure, Energy
  - Each category: list of units with conversion factor relative to base unit
  - Temperature: special handling (Celsius, Fahrenheit, Kelvin — not simple multiplication)
  - Target: 10+ units per category = 100+ total
- [ ] Build converter UI (`feature/converter/`)
  - `ConverterScreen.kt` — two-panel layout: input value + unit on top, result + unit on bottom. Swap button to reverse.
  - `CategorySelector.kt` — horizontal scrollable chips or dropdown for category selection
  - `UnitPicker.kt` — searchable dropdown for unit selection within category
  - `ConverterViewModel.kt` — state: selectedCategory, fromUnit, toUnit, inputValue, result. Auto-convert on input change.
- [ ] All conversions work offline (R17) — static data, no API calls

**Acceptance Criteria:**
- [ ] R15: all 10 categories present with correct conversions
- [ ] R16: 100+ units total
- [ ] R17: works fully offline
- [ ] Temperature conversions are accurate (non-linear)

---

#### Phase 5: Graphing (~5 days)

**Goal:** Interactive 2D function plotter with zoom, pan, and trace.

**Tasks:**
- [ ] Build graph engine (`feature/graphing/GraphCanvas.kt`)
  - Custom `Canvas` composable: draw axes with labels, grid lines
  - Plot function: evaluate expression at N sample points across visible x-range, connect with `drawPath` (cubic bezier interpolation for smooth curves)
  - Handle discontinuities: detect NaN/Infinity, break path at those points (e.g., tan(x) at pi/2)
  - Pinch-to-zoom: `transformable` modifier with scale gestures → adjust x/y range
  - Pan: drag gesture → shift x/y range
  - Performance: evaluate on background thread (coroutine), cache path, only re-evaluate on range change
- [ ] Build graph controls (`feature/graphing/GraphControls.kt`)
  - Text field for function input (e.g., `sin(x)`, `x^2-3x+1`)
  - Parse using same Lexer/Parser from Phase 1, with `x` as variable
  - Support multiple functions: list of (expression, color) pairs
  - Add/remove function buttons
  - Color picker: small palette of 6-8 distinct colors
- [ ] Build trace overlay (`feature/graphing/TraceOverlay.kt`)
  - On tap: find nearest x-coordinate, evaluate f(x), show floating (x, y) label
  - Crosshair lines at trace point
- [ ] `GraphViewModel.kt`
  - State: list of functions, viewport (xMin, xMax, yMin, yMax), tracePoint, isTracing
  - Actions: AddFunction, RemoveFunction, UpdateFunction, SetViewport, Trace, ResetZoom
- [ ] `GraphScreen.kt` — compose canvas + controls + trace

**Acceptance Criteria:**
- [ ] R8: plots y=f(x) with smooth curves, zoom and pan work
- [ ] R9: at least 4 simultaneous graphs with distinct colors
- [ ] R10: tap to see (x, y) coordinates
- [ ] R11: polynomial, trig, exponential, log functions all render correctly
- [ ] Discontinuities handled gracefully (no wild lines between asymptotes)

---

#### Phase 6: Equation Solver (~3 days)

**Goal:** Solve linear, quadratic, and systems of linear equations with step display.

**Tasks:**
- [ ] Implement solvers (`core/math/EquationSolver.kt`)
  - `solveLinear(a, b)` — solve ax + b = 0. Handle a=0 (no solution or infinite)
  - `solveQuadratic(a, b, c)` — quadratic formula with discriminant check. Return: two real roots, one repeated root, or two complex roots. Generate step-by-step: discriminant calc, formula application, simplification
  - `solveSystem2x2(coefficients)` — Cramer's rule or Gaussian elimination. Handle: no solution (parallel lines), infinite solutions (same line)
  - `solveSystem3x3(coefficients)` — Gaussian elimination with partial pivoting. Handle degenerate cases.
- [ ] Build solver UI (`feature/solver/`)
  - `SolverScreen.kt` — tab selector: Linear | Quadratic | System
  - `LinearSolverPanel.kt` — input fields for a, b in ax + b = 0. Show result.
  - `QuadraticSolverPanel.kt` — input fields for a, b, c. Show discriminant, roots, step-by-step solution with animated reveal.
  - `SystemSolverPanel.kt` — grid of input fields for 2x2 or 3x3 system. Toggle between 2x2 and 3x3. Show solution vector.
  - `SolverViewModel.kt` — state per solver type, validate inputs, compute on input change
- [ ] Unit tests for all solver edge cases
  - Zero coefficients, complex roots, degenerate systems, very large numbers

**Acceptance Criteria:**
- [ ] R12: linear equations solved correctly
- [ ] R13: quadratic with step-by-step display, handles complex roots
- [ ] R14: 2x2 and 3x3 systems with edge case handling

---

#### Phase 7: Ad Monetization (~2 days)

**Goal:** Integrate AdMob with respectful, non-intrusive ad placement.

**Tasks:**
- [ ] Set up AdMob SDK (`ads/`)
  - Add `com.google.android.gms:play-services-ads` dependency
  - Initialize `MobileAds` in `CalcMateApp.kt`
  - `AdManager.kt` — singleton managing ad lifecycle, test ad IDs for development
- [ ] Banner ad (`ads/BannerAdComposable.kt`)
  - `AndroidView` wrapper hosting `AdView`
  - Adaptive banner at bottom of calculator screen
  - Does NOT overlap buttons — placed below keypad with proper spacing (R24, R28)
  - Graceful fallback if ad fails to load (hide container)
- [ ] Interstitial ad (`ads/InterstitialAdHelper.kt`)
  - Session counter in ViewModel — show interstitial after every 5th "Equals" press across app lifetime, NOT during active input (R25, R28)
  - Preload next interstitial after showing one
  - No interstitial on first session (good first impression)
- [ ] Rewarded ad (`ads/RewardedAdHelper.kt`)
  - Trigger: "Export history as CSV" feature behind rewarded video (R26)
  - Show reward dialog → play video → unlock export
  - Handle: user cancels, ad not loaded, reward granted
- [ ] Premium IAP (R27)
  - Google Play Billing Library integration
  - One-time purchase to remove all ads
  - Check purchase state on app start, hide all ad composables if purchased
  - `SettingsDialog.kt` — "Remove Ads" button linking to purchase flow

**Acceptance Criteria:**
- [ ] R24: banner visible but non-intrusive
- [ ] R25: interstitial only after 5th session, never during input
- [ ] R26: rewarded video unlocks history export
- [ ] R27: premium purchase removes all ads
- [ ] R28: zero ads during active input or equals press
- [ ] Ads degrade gracefully offline (R32)

---

#### Phase 8: Polish, Testing & Release (~4 days)

**Goal:** Production-quality app ready for Play Store.

**Tasks:**
- [ ] Animations (R23)
  - Crossfade/slide transitions between screens
  - Button press scale animation
  - History sheet spring animation
  - Graph zoom/pan smooth interpolation
- [ ] Haptic feedback (R22)
  - `HapticFeedback.kt` — light vibration on button press, medium on equals, none on scroll
  - Respect user preference toggle
- [ ] App size optimization (R31)
  - Enable R8/ProGuard minification
  - Remove unused resources
  - Verify APK < 15MB
- [ ] Accessibility
  - Content descriptions on all buttons
  - Sufficient color contrast in both themes
  - TalkBack navigation support
- [ ] Testing
  - Unit tests: parser, evaluator, solvers, converters (aim 90%+ coverage on core/)
  - UI tests: calculator flow, navigation, landscape rotation
  - Manual testing: all R1-R33 requirements checklist
- [ ] Play Store preparation
  - App icon (Material You style)
  - Feature graphic (1024x500)
  - Screenshots: calculator, graphing, solver, converter (phone + tablet)
  - Description with keywords (ASO): "scientific calculator", "graphing calculator", "unit converter", "equation solver", "Material You"
  - Privacy policy (no user data collected beyond ad SDK)
  - Set up AdMob production ad units (replace test IDs)
- [ ] Release
  - Signed release APK/AAB
  - Internal testing track first → closed beta → production rollout

**Acceptance Criteria:**
- [ ] R31: APK under 15MB
- [ ] R18-R23: all design/UX requirements met
- [ ] All R1-R33 requirements verified
- [ ] Play Store listing complete with all assets

## Alternative Approaches Considered

| Alternative | Why Rejected |
|---|---|
| Flutter cross-platform | Weaker Material You integration, user's strongest skill is Kotlin/Compose (see origin) |
| mXparser for expression parsing | Requires paid commercial license for ad-supported apps |
| exp4j for expression parsing | Unmaintained, limited function set |
| Vico/MPAndroidChart for graphing | Designed for data visualization, not math function plotting with continuous curves |
| Multi-module architecture | Unnecessary complexity for v1 single-dev project |
| Phased release (core first, graphing later) | User explicitly chose full feature set in v1 (see origin) |

## System-Wide Impact

### Interaction Graph

Calculator action → ViewModel processes → Parser evaluates → Result displayed → History saved (Room) → Session counter increments → Ad trigger check → Banner refreshes / Interstitial shows

### Error & Failure Propagation

- **Parser errors** (invalid expression) → caught in ViewModel → display error message in ExpressionDisplay, do NOT increment session counter
- **Math errors** (div/0, domain) → Evaluator returns NaN/Infinity → Formatter shows "Error" or "Undefined"
- **Ad load failure** → AdManager logs, hides ad container → no user-visible error
- **Room write failure** → HistoryRepository catches, logs → calculator still works, history entry lost silently
- **IAP verification failure** → keep showing ads, retry verification on next launch

### State Lifecycle Risks

- **Process death during calculation** → ViewModel state lost → SavedStateHandle preserves current expression
- **Rotation during graphing** → ViewModel survives rotation (Compose handles this), Canvas redraws
- **History DB corruption** → destructive migration strategy (clear history, app still works)

### Integration Test Scenarios

1. Enter complex expression → equals → verify history saved → rotate → verify history still shows → tap history entry → verify expression reloaded
2. Enter 5 calculations → verify interstitial triggers on 5th → verify no interstitial during active input
3. Plot sin(x) → zoom in → add cos(x) → trace → verify coordinates correct at known points (0, pi/2, pi)
4. Solve quadratic with complex roots → verify step display shows discriminant < 0 → verify complex root format
5. Convert 100°C to °F → verify 212°F → swap units → verify reverse conversion

## Acceptance Criteria

### Functional Requirements (from origin R1-R33)

- [ ] R1-R7: Core calculator with natural display
- [ ] R8-R11: Graphing with zoom, pan, trace, multi-function
- [ ] R12-R14: Equation solver (linear, quadratic, systems)
- [ ] R15-R17: Unit converter (10 categories, 100+ units, offline)
- [ ] R18-R23: Material You design, dark mode, landscape, haptics, animations
- [ ] R24-R28: Respectful ad placement, rewarded video, premium IAP
- [ ] R29-R33: Kotlin/Compose, API 24+, <15MB, offline-first, AdMob

### Non-Functional Requirements

- [ ] App cold start < 2 seconds
- [ ] Expression evaluation < 100ms for standard expressions
- [ ] Graph rendering < 500ms for single function at default zoom
- [ ] No ANR (Application Not Responding) errors
- [ ] Memory usage < 100MB during graphing

### Quality Gates

- [ ] Unit test coverage > 90% on `core/` package
- [ ] All Compose UI previews render without errors
- [ ] Manual testing of all 33 requirements
- [ ] ProGuard/R8 does not break any functionality
- [ ] Tested on API 24, 28, 31, 34 emulators

## Success Metrics (from origin)

- 10,000+ downloads within first 3 months (organic + ASO)
- 4.5+ star rating on Play Store
- Average daily session time > 2 minutes
- Ad revenue covering $25 developer fee within first month
- 30-day retention > 20%

## Dependencies & Prerequisites

- Google Play Developer account ($25 one-time) — required before release
- Google AdMob account — required before Phase 7
- Android Studio latest stable
- Physical device for ad testing (emulator limitations with AdMob)

## Scope Boundaries (from origin)

- No iOS version
- No CAS / symbolic math
- No matrix/vector operations
- No programming mode (binary/octal/hex)
- No cloud sync or accounts
- No 3D graphing

## Risk Analysis & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Custom parser bugs with edge cases | Medium | High | Extensive unit test suite, fuzzing with random expressions |
| Graph discontinuity rendering glitches | Medium | Medium | Detect NaN/Infinity, break path, test known discontinuous functions |
| AdMob policy rejection | Low | High | Follow all AdMob policies, no accidental clicks, proper ad spacing |
| Play Store review rejection | Low | Medium | Follow all content policies, proper privacy policy, no deceptive practices |
| App size exceeds 15MB | Low | Low | R8 minification, monitor size in CI, strip unused resources |
| Low initial downloads | High | Medium | Strong ASO, polished screenshots, consider a launch promo or Product Hunt post |

## Estimated Timeline

| Phase | Duration | Cumulative |
|---|---|---|
| Phase 1: Core Engine | 5 days | Day 5 |
| Phase 2: Calculator UI | 4 days | Day 9 |
| Phase 3: History | 2 days | Day 11 |
| Phase 4: Unit Converter | 3 days | Day 14 |
| Phase 5: Graphing | 5 days | Day 19 |
| Phase 6: Equation Solver | 3 days | Day 22 |
| Phase 7: Ad Monetization | 2 days | Day 24 |
| Phase 8: Polish & Release | 4 days | Day 28 |
| **Total** | **~28 days** | **~4 weeks** |

## Dependencies (build.gradle.kts)

```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2026.02.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose:2.8.x")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.x")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.x")

// Room (history persistence)
implementation("androidx.room:room-runtime:2.7.x")
implementation("androidx.room:room-ktx:2.7.x")
ksp("androidx.room:room-compiler:2.7.x")

// DataStore (preferences)
implementation("androidx.datastore:datastore-preferences:1.1.x")

// AdMob
implementation("com.google.android.gms:play-services-ads:23.x.x")

// Google Play Billing (IAP)
implementation("com.android.billingclient:billing-ktx:7.x.x")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.x")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

## Sources & References

### Origin

- **Origin document:** [docs/brainstorms/2026-03-26-scientific-calculator-app-requirements.md](docs/brainstorms/2026-03-26-scientific-calculator-app-requirements.md) — Key decisions carried forward: Kotlin+Compose stack, full feature set in v1, Material You as primary differentiator, respectful ad placement strategy.

### External References

- [Jetpack Compose Canvas Graphics](https://developer.android.com/develop/ui/compose/graphics/draw/overview) — Canvas API for custom graphing
- [mXparser License](https://mathparser.org/mxparser-license/) — Commercial license required (reason for custom parser)
- [AdMob + Compose Integration](https://dev.to/trinadhthatakula/jetpack-compose-admob-made-easy-modern-reusable-kotlin-composables-for-android-developers-4g71) — Banner/Interstitial/Rewarded patterns
- [AdMob Next Gen SDK](https://www.droiddevtips.com/google-admob-next-gen-sdk-implementing-inline-adaptive-banners-in-jetpack-compose.html) — Adaptive banner implementation
- [Recursive Descent Parser for Calculator](https://kodaschool.com/blog/developing-a-scientific-calculator-using-jetpack-and-kotlin) — Architecture reference
- [Compose Calculator MVVM Example](https://github.com/pablin202/compose-calculator) — MVVM pattern reference
