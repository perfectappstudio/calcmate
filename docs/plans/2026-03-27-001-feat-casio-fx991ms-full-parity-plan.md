---
title: "feat: Casio fx-991MS Full Function Parity"
type: feat
status: in-progress
date: 2026-03-27
origin: docs/brainstorms/2026-03-26-casio-fx991ms-parity-requirements.md
---

# feat: Casio fx-991MS Full Function Parity

## Overview

Add all 35 missing features from the Casio fx-991MS (2nd edition, S-V.P.A.M.) to CalcMate. This makes CalcMate a complete replacement for the world's most popular scientific calculator, while keeping its modern Material You UI, graphing, and history features that the Casio lacks.

## Problem Statement

CalcMate covers basic scientific functions but is missing the advanced modes (CMPLX, SD, REG, BASE, MAT, VCT), memory system (Ans, variables, M+/M-), calculus (integration/differentiation), and display features (engineering notation, DMS, percent) that students and engineers depend on daily. (see origin: docs/brainstorms/2026-03-26-casio-fx991ms-parity-requirements.md)

## Critical Architecture Decisions

These resolve the 5 critical questions from specflow analysis. All follow Casio behavior:

| Decision | Resolution |
|---|---|
| **Mode lifecycle** | Entering a mode clears current expression. Ans preserved. Variables preserved. SD/REG data cleared on mode switch. |
| **Evaluation return type** | New sealed class `CalcResult` with subtypes: `RealResult(Double)`, `ComplexResult(real, imag)`, `MatrixResult(rows)`, `VectorResult(components)`, `IntegerResult(Long, base)` |
| **Variable E vs Euler's e** | Uppercase via STO/RCL = variable. Pi/e buttons = constants. Parser uses context. |
| **BASE mode range** | 32-bit signed: DEC -2147483648..2147483647. BIN/OCT/HEX use two's complement for negatives. |
| **Percent operator** | Postfix unary: `x%` = `x/100`. Markup/discount follows naturally from operator precedence. |
| **Mode navigation** | Mode selector chip row at top of Calculator tab. Not new bottom nav tabs. |
| **CODATA version** | Use CODATA 2018 recommended values. Note in UI that values may differ slightly from physical Casio (2010). |

## Implementation Phases

### Phase 1: Core Architecture Refactor (Foundation)

**Goal:** Extend the parser/evaluator to support new types, add memory system, expand angle units.

#### Unit 1.1: CalcResult Sealed Class & Evaluator Refactor

**Files:**
- `core/parser/CalcResult.kt` (new)
- `core/parser/Evaluator.kt` (major refactor)
- `core/parser/Formatter.kt` (extend for new types)
- `core/model/AngleUnit.kt` (add GRADIAN)

**Approach:**
- Create `CalcResult` sealed class: `RealResult(value: Double)`, `ComplexResult(real: Double, imag: Double)`, `IntegerResult(value: Long, base: NumberBase)`, `MatrixResult(data: Array<DoubleArray>, rows: Int, cols: Int)`, `VectorResult(components: DoubleArray)`
- Refactor `Evaluator.evaluate()` to return `CalcResult` instead of `Double`
- All existing call sites (CalculatorViewModel, GraphViewModel) must adapt to unwrap `RealResult`
- Add `GRADIAN` to `AngleUnit` enum, update trig conversion in Evaluator
- Extend `Formatter` to format each `CalcResult` subtype

**Verification:** All existing 100+ unit tests still pass after refactor. New tests for `CalcResult` formatting.

**Execution note:** Characterization-first — capture existing evaluator behavior with tests before refactoring.

#### Unit 1.2: Memory & Variable System

**Files:**
- `core/model/MemoryManager.kt` (new)
- `core/model/CalculatorAction.kt` (extend)
- `core/model/CalculatorState.kt` (extend)
- `core/parser/Token.kt` (add VARIABLE, ANS tokens)
- `core/parser/Lexer.kt` (recognize variables)
- `core/parser/Parser.kt` (parse variables)
- `core/parser/Evaluator.kt` (evaluate variables from memory)

**Approach:**
- `MemoryManager` singleton/object: holds `ans: CalcResult`, `variables: Map<Char, CalcResult>` (A-F, M, X, Y), `independentM: Double`
- On Equals: store result in `ans`
- STO action: store current result in named variable
- RCL action: recall variable value into expression
- M+ action: add current result to M. M- subtracts. Show "M" indicator when M != 0.
- Ans defaults to `RealResult(0.0)` on fresh start
- Variables persist until explicit clear or app restart
- Add `ANS` and `VARIABLE` token types to Lexer/Parser/Evaluator

**Verification:** Test Ans chaining, variable STO/RCL, M+/M- accumulation, Ans default = 0.

#### Unit 1.3: Percent Operator & Random Number

**Files:**
- `core/parser/Token.kt` (add PERCENT postfix)
- `core/parser/Lexer.kt`
- `core/parser/Parser.kt` (postfix % after factorial)
- `core/parser/Evaluator.kt` (% = divide by 100)
- `core/math/RandomUtil.kt` (new)

**Approach:**
- `%` as postfix unary: `x%` evaluates to `x/100`. This makes `500*10%` = `500*0.1` = 50, `500+500*10%` = 550 (markup), etc.
- `Ran#` function generates `kotlin.random.Random.nextDouble(0.0, 1.0)`, truncated to 3 decimals

**Verification:** All 7 Casio percent examples (p.20-22) produce correct results.

### Phase 2: Display & Number Formats

#### Unit 2.1: Fix/Sci/Norm Display Settings

**Files:**
- `core/model/DisplaySettings.kt` (new)
- `core/model/CalculatorState.kt` (replace DisplayFormat with DisplaySettings)
- `core/parser/Formatter.kt` (implement Fix(0-9), Sci(0-9), Norm(1/2))
- `ui/components/SettingsDialog.kt` (extend)
- `core/parser/RoundingUtil.kt` (new — Rnd function)

**Approach:**
- `DisplaySettings(mode: DisplayMode, digits: Int)` where `DisplayMode` = FIX, SCI, NORM_1, NORM_2
- FIX(n): round to n decimal places. SCI(n): n significant digits in scientific notation.
- Norm 1: scientific for |x| < 10^-2 or |x| >= 10^10. Norm 2: scientific for |x| < 10^-9 or |x| >= 10^10.
- `Rnd(x)`: round x to current display setting precision

**Verification:** Examples from p.18 (200/7*14 with Fix 3 = 400.000).

#### Unit 2.2: Engineering Notation

**Files:**
- `core/parser/Formatter.kt` (add engineering format)
- `core/model/DisplaySettings.kt` (add engOn flag)
- `feature/calculator/ScientificKeypad.kt` (add ENG button)

**Approach:**
- ENG shifts exponent to nearest multiple of 3 (e.g., 56088 -> 56.088×10^3)
- ENG← (shift+ENG) shifts the other direction
- When EngON: display engineering symbol suffix (k, M, G, T, m, μ, n, p, f) instead of ×10^n
- Add ENG button to scientific function row

**Verification:** Examples from p.23-25 (56088 -> 56.088×10^03, 0.08125 -> 81.25×10^-03).

#### Unit 2.3: Sexagesimal (DMS) Input & Arithmetic

**Files:**
- `core/parser/Token.kt` (add DMS token)
- `core/parser/Lexer.kt` (recognize DMS input)
- `core/parser/DmsUtil.kt` (new)
- `feature/calculator/ScientificKeypad.kt` (add DMS button)

**Approach:**
- DMS input syntax: `degrees°minutes'seconds"` or via dedicated DMS button that inserts separator
- `DmsUtil`: convert DMS to decimal, decimal to DMS, add/subtract DMS values
- DMS arithmetic: add/subtract two DMS = DMS result, multiply/divide DMS by scalar = DMS result
- Toggle display between DMS and decimal with DMS button on result

**Verification:** Examples from p.22-23 (2°20'30" + 0°39'30" = 3°0'0, 12°34'56" × 3.45 = 43°24'31.2).

#### Unit 2.4: Fraction Enhancements

**Files:**
- `core/parser/Formatter.kt` (mixed vs improper toggle)
- `core/model/DisplaySettings.kt` (add fractionFormat: MIXED | IMPROPER)
- `ui/components/SettingsDialog.kt` (add fraction format setting)

**Approach:**
- Toggle between ab/c (mixed: 1 2/3) and d/c (improper: 5/3) in settings
- Default: mixed (ab/c), matching Casio default

**Verification:** 1⌋2⌋3 displays as "1 2/3" in ab/c mode, "5/3" in d/c mode.

### Phase 3: Advanced Calculator Modes

#### Unit 3.1: Complex Number Mode (CMPLX)

**Files:**
- `core/model/ComplexState.kt` (new)
- `core/model/ComplexAction.kt` (new)
- `core/math/ComplexMath.kt` (new)
- `core/parser/ComplexEvaluator.kt` (new — walks AST, returns ComplexResult)
- `feature/complex/ComplexScreen.kt` (new)
- `feature/complex/ComplexViewModel.kt` (new)
- `feature/complex/ComplexKeypad.kt` (new)

**Approach:**
- `ComplexMath`: add, subtract, multiply, divide, conjugate, abs (magnitude), arg (argument), polar↔rectangular conversion
- `ComplexEvaluator`: extends the standard evaluator but returns `ComplexResult`. Recognizes `i` literal and `∠` operator for polar input.
- UI: calculator keypad with added `i` and `∠` buttons, Conjg/Abs/arg in shift functions
- Toggle result format: a+bi ↔ r∠θ via Re⇔Im button
- Append `►a+bi` or `►r∠θ` to force output format

**Verification:** (2+6i)÷(2i) = 3-i, √2∠45 = 1+i, Conjg(2+3i) = 2-3i, Abs(1+i) = 1.414213562.

#### Unit 3.2: Numerical Integration & Differentiation

**Files:**
- `core/math/NumericalCalculus.kt` (new)
- `core/parser/Token.kt` (add INTEGRATE, DIFFERENTIATE tokens)
- `core/parser/Lexer.kt` / `Parser.kt` / `Evaluator.kt` (integrate/differentiate as functions)
- `feature/calculator/ScientificKeypad.kt` (add ∫dx and d/dx buttons)

**Approach:**
- Integration: Simpson's rule with N = 2^n partitions. Input: expression, a, b, optional n (default 6).
- Differentiation: central difference (f(a+h) - f(a-h))/(2h). Input: expression, point a, optional Δx.
- Both are special functions in the parser that take comma-separated arguments
- Evaluate on background coroutine (can be slow)

**Verification:** ∫₁⁵(2x²+3x+8)dx = 150.6666667 (n=6), d/dx(3x²-5x+2) at x=2 = 7.

#### Unit 3.3: Rectangular-Polar Conversion

**Files:**
- `core/math/CoordinateConversion.kt` (new)
- `core/parser/Token.kt` (add POL, REC tokens)
- `core/parser/Evaluator.kt` (handle Pol/Rec as two-arg functions)

**Approach:**
- `Pol(x, y)` -> stores r in variable E, θ in variable F, displays r
- `Rec(r, θ)` -> stores x in variable E, y in variable F, displays x
- θ result range: -180° < θ ≤ 180°

**Verification:** Pol(1, √3) with RAD -> r=2, θ=1.047197551. Rec(2, 60°) with DEG -> x=1, y=1.732050808.

#### Unit 3.4: Angle Unit Conversion (DRG►)

**Files:**
- `core/math/AngleConversion.kt` (new)
- `feature/calculator/ScientificKeypad.kt` (add DRG► button)

**Approach:**
- Convert the currently displayed value from its assumed unit to a selected target unit
- Menu: D (Deg), R (Rad), G (Gra) — select target unit
- 90° = π/2 rad = 100 gra

**Verification:** 4.25 rad -> 243.5070629° (Deg mode, convert from Rad).

### Phase 4: Statistics (SD & REG Modes)

#### Unit 4.1: Statistics Engine

**Files:**
- `core/math/StatisticsEngine.kt` (new)

**Approach:**
- Stores list of `DataPoint(value: Double, frequency: Int)` for SD, `DataPair(x: Double, y: Double, frequency: Int)` for REG
- Computes: n, Σx, Σx², x̄, σₓ (population), sₓ (sample), and for REG: Σy, Σy², Σxy, ȳ, σᵧ, sᵧ
- Regression: compute A, B, (C for quad), r, x̂, ŷ for all 6 types (Lin, Log, Exp, Pwr, Inv, Quad)
- Normal distribution: P(t), Q(t), R(t), x►t conversion using the error function

**Verification:** SD example (p.47): data {55,54,51,55,53,53,54,52} -> x̄=53.375, sₓ=1.407885953. REG linear example (p.51): A=997.4, B=0.56, r=0.982607368.

#### Unit 4.2: SD Mode Screen

**Files:**
- `core/model/StatisticsState.kt` (new)
- `core/model/StatisticsAction.kt` (new)
- `feature/statistics/SDScreen.kt` (new)
- `feature/statistics/SDViewModel.kt` (new)
- `feature/statistics/DataEntryList.kt` (new — reusable)

**Approach:**
- Top: mode indicator "SD"
- Data entry: scrollable list of entered values with frequency column
- "Add" FAB to add new data point, swipe-to-delete existing
- Recall bar: chips for n, x̄, σₓ, sₓ, Σx, Σx² that insert the value
- Normal distribution: DISTR button -> P(t), Q(t), R(t), ►t sub-menu

#### Unit 4.3: REG Mode Screen

**Files:**
- `feature/statistics/REGScreen.kt` (new)
- `feature/statistics/REGViewModel.kt` (new)

**Approach:**
- Top: regression type selector (Lin/Log/Exp/Pwr/Inv/Quad segmented buttons)
- Data entry: two-column scrollable list (x, y) with frequency
- Recall bar: chips for A, B, C, r, x̂, ŷ, plus all x/y statistics
- Reuse `DataEntryList` from SD with paired mode

### Phase 5: Base-N Mode

#### Unit 5.1: Base-N Engine & Parser

**Files:**
- `core/math/BaseNEngine.kt` (new)
- `core/parser/Token.kt` (add AND, OR, XOR, XNOR, NOT, NEG, BASE_SUFFIX tokens)
- `core/parser/Lexer.kt` (hex A-F recognition, 0x/0b/0o prefixes, d/h/b/o suffixes)
- `core/parser/Parser.kt` (logical operators at precedence 12-13)
- `core/parser/Evaluator.kt` (integer arithmetic with logical ops)

**Approach:**
- `BaseNEngine`: convert between bases, validate ranges, perform logical operations
- Logical op precedence: AND (12), OR/XOR/XNOR (13) — below arithmetic, matching Casio
- NOT and NEG are prefix unary operators
- All values clamped to 32-bit signed range
- Mixed-base expressions: `d10 + h10 + b10 + o10` = 36 (decimal)

#### Unit 5.2: Base-N Screen

**Files:**
- `core/model/BaseNState.kt` (new)
- `feature/basen/BaseNScreen.kt` (new)
- `feature/basen/BaseNViewModel.kt` (new)
- `feature/basen/BaseNKeypad.kt` (new)

**Approach:**
- Top: base selector chips (DEC/HEX/OCT/BIN) — selected base shown prominently
- Keypad adapts: BIN shows only 0,1; OCT shows 0-7; DEC shows 0-9; HEX shows 0-9+A-F
- Extra row for logical operators: AND, OR, XOR, NOT, NEG
- Result display shows value in selected base
- Quick-convert: tap base chip to convert displayed result

### Phase 6: Matrix & Vector Modes

#### Unit 6.1: Matrix Engine

**Files:**
- `core/math/MatrixEngine.kt` (new)

**Approach:**
- Support up to 3×3 matrices. Operations: +, -, ×, scalar×, determinant, transpose, inverse, square, cube, abs (element-wise)
- Three named matrix slots: MatA, MatB, MatC. MatAns for results.
- Use Gaussian elimination for determinant and inverse

#### Unit 6.2: Matrix Screen

**Files:**
- `core/model/MatrixState.kt` (new)
- `feature/matrix/MatrixScreen.kt` (new)
- `feature/matrix/MatrixViewModel.kt` (new)
- `feature/matrix/MatrixEditor.kt` (new — grid input)
- `feature/matrix/MatrixKeypad.kt` (new)

**Approach:**
- Dimension picker: rows × cols dropdowns (1-3 each)
- Grid editor: tappable cells, each opens a number input, navigate with arrow buttons
- Expression area: type "MatA × MatB" using dedicated Mat buttons
- Result: scrollable grid display for MatAns

#### Unit 6.3: Vector Engine & Screen

**Files:**
- `core/math/VectorEngine.kt` (new)
- `core/model/VectorState.kt` (new)
- `feature/vector/VectorScreen.kt` (new)
- `feature/vector/VectorViewModel.kt` (new)
- `feature/vector/VectorEditor.kt` (new)

**Approach:**
- 2D or 3D vectors. Three named slots: VctA, VctB, VctC. VctAns for results.
- Operations: +, -, scalar×, dot product (·), cross product (×, 3D only), magnitude (Abs)
- Dimension selector: 2D or 3D
- Editor: 2-3 component input fields

### Phase 7: Equation Solver Enhancements

#### Unit 7.1: Cubic Equation Solver

**Files:**
- `core/math/EquationSolver.kt` (extend)
- `feature/solver/CubicSolverPanel.kt` (new)
- `feature/solver/SolverScreen.kt` (extend segmented buttons)
- `core/model/SolverState.kt` (extend)

**Approach:**
- Add `solveCubic(a, b, c, d): CubicResult` using Cardano's formula
- Handle: 3 real roots, 1 real + 2 complex conjugate roots
- Step-by-step display matching quadratic pattern
- Add "Cubic" tab to solver segmented buttons

**Verification:** x³-2x²-x+2=0 -> x=2, -1, 1.

#### Unit 7.2: Newton's Method SOLVE

**Files:**
- `core/math/NewtonSolver.kt` (new)
- `feature/solver/NewtonSolverPanel.kt` (new)

**Approach:**
- User inputs: expression (as string), target variable, initial guess
- Newton's method: x_{n+1} = x_n - f(x_n)/f'(x_n), using numerical differentiation for f'
- Max 200 iterations, convergence threshold 1e-12
- Failure modes: "Can't solve" error if diverges, oscillates, or hits zero derivative

**Verification:** y = ax² + b for x when y=0, a=1, b=-2 -> x = ±1.414213562.

### Phase 8: Constants, Conversions & Polish

#### Unit 8.1: Expand Scientific Constants to 40

**Files:**
- `core/math/Constants.kt` (extend)
- `ui/components/ConstantsSheet.kt` (new — searchable list)

**Approach:**
- Add all 40 CODATA 2018 constants with name, symbol, value, unit
- Searchable/filterable bottom sheet for constant selection
- Categorize: Particle masses, Electromagnetic, Thermodynamic, Atomic, Universal

#### Unit 8.2: Expand Metric Conversions

**Files:**
- `core/data/UnitData.kt` (extend)

**Approach:**
- Add missing Casio conversions not already in the 94-unit set: pc↔km, nautical mile, kgf·m↔J, lbf/in²↔kPa, hp↔kW, etc.
- Verify all 40 Casio conversion pairs are present (some may already exist)

#### Unit 8.3: CALC Mode & Multi-Statements

**Files:**
- `core/model/CalcModeState.kt` (new)
- `feature/calculator/CalcModeSheet.kt` (new — variable substitution UI)
- `core/parser/Lexer.kt` (recognize colon separator)
- `core/parser/MultiStatementEvaluator.kt` (new)

**Approach:**
- CALC: user enters expression with variables, presses CALC, bottom sheet prompts for each variable value, evaluates, allows re-substitution
- Multi-statements: colon (`:`) separates expressions, evaluated left-to-right, intermediate results stored in Ans, final result displayed

#### Unit 8.4: Mode Navigation UI

**Files:**
- `feature/calculator/ModeSelector.kt` (new — chip row)
- `feature/calculator/CalculatorScreen.kt` (extend)
- `ui/navigation/AppNavigation.kt` (extend routing)

**Approach:**
- Add a horizontal chip row below the top bar on Calculator tab: COMP (default), CMPLX, SD, REG, BASE, MAT, VCT
- Selecting a mode swaps the keypad and display area for the mode's screen
- Mode indicator shown prominently
- Entering a mode clears current expression (Casio behavior)
- Ans and variables preserved across mode switches

### Phase 9: Keypad & UI Updates

#### Unit 9.1: Extended Calculator Keypad

**Files:**
- `feature/calculator/ScientificKeypad.kt` (extend)
- `feature/calculator/ScientificKeypadLand.kt` (extend)

**Approach:**
Add buttons for all new functions. In portrait, use scrollable rows or a "more" panel:
- Row additions: ENG, DMS (°'"), %, Ran#, Rnd
- Memory row: STO, RCL, M+, M-, Ans
- Shift functions: ∫dx, d/dx, Pol, Rec, DRG►, CALC, SOLVE
- Mode-specific keypads (CMPLX, BASE, MAT, VCT) defined in their own screen files

#### Unit 9.2: Updated Settings Dialog

**Files:**
- `ui/components/SettingsDialog.kt` (extend)

**Approach:**
- Add: Fix/Sci/Norm mode with digit count picker
- Add: EngON/EngOFF toggle
- Add: Fraction format ab/c vs d/c
- Add: Complex format a+bi vs r∠θ
- Organize into collapsible sections

### Phase 10: Tests

#### Unit 10.1: Unit Tests for All New Math

**Files:**
- `test/.../math/ComplexMathTest.kt`
- `test/.../math/NumericalCalculusTest.kt`
- `test/.../math/StatisticsEngineTest.kt`
- `test/.../math/BaseNEngineTest.kt`
- `test/.../math/MatrixEngineTest.kt`
- `test/.../math/VectorEngineTest.kt`
- `test/.../math/CoordinateConversionTest.kt`
- `test/.../math/NewtonSolverTest.kt`
- `test/.../parser/EvaluatorExtendedTest.kt` (percent, Ans, variables, Rnd)

**Approach:** Every example from the Casio manual (pages 16-68) becomes a test case. ~150 new tests.

#### Unit 10.2: UI Tests for New Modes

**Files:**
- `androidTest/.../ComplexScreenTest.kt`
- `androidTest/.../StatisticsScreenTest.kt`
- `androidTest/.../BaseNScreenTest.kt`
- `androidTest/.../MatrixScreenTest.kt`
- `androidTest/.../VectorScreenTest.kt`

**Approach:** Verify mode switching, data entry, and result display for each new mode.

## Requirements Trace

| Requirement | Implementation Unit |
|---|---|
| R1 (Gradian) | 1.1 |
| R2 (DRG►) | 3.4 |
| R3 (DMS) | 2.3 |
| R4 (Pol/Rec) | 3.3 |
| R5 (Percent) | 1.3 |
| R6 (Mixed/Improper) | 2.4 |
| R7 (Engineering) | 2.2 |
| R8 (Rnd) | 2.1 |
| R9-R11 (Complex) | 3.1 |
| R12 (Integration) | 3.2 |
| R13 (Differentiation) | 3.2 |
| R14-R15 (SD) | 4.1, 4.2 |
| R16-R19 (REG) | 4.1, 4.3 |
| R20 (Normal dist) | 4.1, 4.2 |
| R21-R23 (Base-N) | 5.1, 5.2 |
| R24 (Cubic) | 7.1 |
| R25 (SOLVE) | 7.2 |
| R26 (Matrix) | 6.1, 6.2 |
| R27 (Vector) | 6.3 |
| R28 (Ans) | 1.2 |
| R29 (Variables) | 1.2 |
| R30 (M+/M-) | 1.2 |
| R31 (CALC) | 8.3 |
| R32 (Multi-stmt) | 8.3 |
| R33 (Ran#) | 1.3 |
| R34 (40 constants) | 8.1 |
| R35 (40 conversions) | 8.2 |

## Dependencies Between Phases

```
Phase 1 (Foundation) ──┬── Phase 2 (Display) ──── Phase 9 (Keypad UI)
                       ├── Phase 3 (Adv Modes) ─┐
                       ├── Phase 4 (Statistics)  ├── Phase 8 (Polish)
                       ├── Phase 5 (Base-N)     │
                       ├── Phase 6 (Matrix/Vec) │
                       └── Phase 7 (Eq Solver)  ┘
                                                 └── Phase 10 (Tests)
```

Phases 3-7 can run **in parallel** after Phase 1 completes. Phase 2 can also run in parallel with 3-7. Phase 8 needs some of 3-7. Phases 9-10 are final.

## Scope Boundaries

(see origin: docs/brainstorms/2026-03-26-casio-fx991ms-parity-requirements.md)
- No physical keypad simulation (SHIFT/ALPHA modifier system)
- No Casio display format replication (two-line dot matrix)
- No Casio-specific limitations (79-step input, 10-level stack)
- No Casio replay/copy system (CalcMate has Room-based history)
- Existing features (graphing, converter UI, history, ads) remain unchanged

## Success Criteria

- Every function in the Casio fx-991MS manual has a working CalcMate equivalent
- All ~80 manual examples produce matching results (within rounding)
- All existing tests continue to pass
- App size remains under 20MB
- No performance regression in basic calculator operations

## Sources & References

### Origin

- **Origin document:** [docs/brainstorms/2026-03-26-casio-fx991ms-parity-requirements.md](docs/brainstorms/2026-03-26-casio-fx991ms-parity-requirements.md) — 35 requirements for full Casio fx-991MS parity. Key decisions: full fx-991MS scope, modes as screens within calculator tab, CODATA 2018.
- **Casio Manual:** /Users/up/Projects/calfunction/fx-100MS_570MS_991MS_EN.pdf (76 pages)

### Internal References

- Parser architecture: `core/parser/Evaluator.kt` (current Double-only return type)
- Feature pattern: `feature/calculator/` (ViewModel + State + Screen + Keypad)
- Navigation: `ui/navigation/AppNavigation.kt` (AnimatedContent routing)
- Existing constants: `core/math/Constants.kt` (10 constants, needs 40)
- Existing solver: `core/math/EquationSolver.kt` (no cubic, no Newton's method)
- Stitch designs: Project ID 4083519555503358549, design system asset 4943356026259302648
