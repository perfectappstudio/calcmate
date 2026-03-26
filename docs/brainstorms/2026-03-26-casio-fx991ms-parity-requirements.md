---
date: 2026-03-26
topic: casio-fx991ms-parity
---

# Casio fx-991MS Full Function Parity

## Problem Frame

CalcMate currently covers basic scientific calculator functions (arithmetic, trig, log, powers, fractions, graphing, equation solver, unit converter). However, it lacks 28 features found on the Casio fx-991MS — the world's most popular scientific calculator for students. Without these, students and engineers who rely on fx-991MS workflows can't fully switch to CalcMate.

**Target:** Feature parity with the Casio fx-100MS/fx-570MS/fx-991MS (2nd edition, S-V.P.A.M.) as documented in the official user guide.

## Requirements

### Angle & Coordinate Systems
- R1. Gradian angle unit: add GRA option alongside DEG/RAD. 90deg = pi/2 rad = 100 gra.
- R2. Angle unit conversion (DRG>): convert a displayed value between Deg, Rad, Gra inline without re-entering it.
- R3. Sexagesimal (DMS) input and arithmetic: input values as Degrees-Minutes-Seconds (e.g., 2 deg 30' 45"), perform add/subtract of DMS values, multiply/divide DMS by decimal, and convert between DMS and decimal.
- R4. Rectangular-Polar coordinate conversion: Pol(x,y) -> (r, theta) and Rec(r, theta) -> (x,y). Store results in variables E and F.

### Number Formats & Display
- R5. Percent calculations: support A*B% (percentage), A/B% (ratio), A*B%+ (markup), A*B%- (discount), rate-of-change calculations as per Casio behavior.
- R6. Mixed fraction <-> improper fraction toggle (ab/c <-> d/c).
- R7. Engineering notation: ENG key shifts decimal point right by 10^3 steps, ENG-back shifts left. Display engineering symbols when EngON: k (10^3), M (10^6), G (10^9), T (10^12), m (10^-3), mu (10^-6), n (10^-9), p (10^-12), f (10^-15).
- R8. Rounding function (Rnd): round intermediate calculation value to current display digits setting (Fix/Sci/Norm).

### Complex Numbers
- R9. Complex number mode (CMPLX): input complex numbers as a+bi or r-angle-theta (polar). Perform +, -, *, / on complex numbers.
- R10. Complex number functions: conjugate (Conjg), absolute value (Abs), argument (arg). Toggle display between real/imaginary parts and r/theta.
- R11. Result format override: append >r-angle-theta or >a+bi to force output format regardless of setting.

### Calculus
- R12. Numerical integration: compute definite integral of f(x) from a to b using Simpson's rule. User specifies expression, limits (a, b), and optional partition count (n). Display result.
- R13. Numerical differentiation: compute d/dx f(x) at point x=a with optional delta-x. Display the derivative value.

### Statistics — Standard Deviation (SD Mode)
- R14. SD mode data entry: input single-variable data with optional frequency using DT key. Support scrolling/editing/deleting entered data.
- R15. SD statistical values: compute and recall n, Sigma-x, Sigma-x^2, x-bar (mean), sigma-x (population std dev), s-x (sample std dev).

### Statistics — Regression (REG Mode)
- R16. Regression types: Linear (y=A+Bx), Logarithmic (y=A+B*ln(x)), Exponential (y=A*e^(Bx)), Power (y=A*x^B), Inverse (y=A+B/x), Quadratic (y=A+Bx+Cx^2).
- R17. Regression data entry: input paired (x, y) data with optional frequency.
- R18. Regression outputs: compute A, B (and C for quadratic), correlation coefficient r, estimated y-hat for given x, estimated x-hat for given y. For quadratic: x-hat-1, x-hat-2.
- R19. Additional REG statistics: x-bar, y-bar, sigma-x, sigma-y, s-x, s-y, Sigma-x^2, Sigma-y^2, Sigma-xy, n.

### Normal Distribution
- R20. Normal distribution calculations: given data entered in SD mode, compute P(t), Q(t), R(t), and x-to-t standardized variate for a given x value. P(t) = probability from -inf to t, Q(t) = probability from 0 to t, R(t) = probability from t to +inf.

### Base-n Calculations
- R21. BASE mode: perform arithmetic in binary (BIN), octal (OCT), decimal (DEC), hexadecimal (HEX). Switch default base, or tag individual values with d/h/b/o suffix for mixed-base expressions.
- R22. Base conversion: convert a displayed result between DEC, HEX, BIN, OCT with a single keypress.
- R23. Logical operations: AND, OR, XOR, XNOR, NOT (bitwise complement), NEG (two's complement negation). Operate on values in any base.

### Equation Solver Enhancements
- R24. Cubic equation solver: solve ax^3 + bx^2 + cx + d = 0. Display up to 3 roots including complex roots (show real and imaginary parts).
- R25. SOLVE function: for an arbitrary expression with variable x, find x using Newton's method. User inputs expression, initial guess, and target variable.

### Matrix Calculations
- R26. Matrix mode (MAT): define up to 3 matrices (MatA, MatB, MatC) of size up to 3x3. Perform: addition, subtraction, multiplication, scalar multiplication, determinant (Det), transpose (Trn), inverse (Mat^-1), square (Mat^2), cube (Mat^3), absolute value of elements (Abs). Store result in MatAns.

### Vector Calculations
- R27. Vector mode (VCT): define up to 3 vectors (VctA, VctB, VctC) of dimension 2 or 3. Perform: addition, subtraction, scalar multiplication, dot product, cross product (3D only), absolute value (magnitude). Store result in VctAns.

### Memory & Expression Features
- R28. Answer Memory (Ans): automatically store last result; recall with Ans key for chaining calculations (e.g., pressing an operator after = uses the previous result).
- R29. Variables (A, B, C, D, E, F, M, X, Y): store and recall 9 named variables using STO and RCL. Persist across calculations within a session.
- R30. Independent memory (M): M+ adds current result to M, M- subtracts from M. Show "M" indicator when M is non-zero. Recall with RCL M.
- R31. CALC function: store an expression containing variables, then repeatedly substitute different values and evaluate without re-entering the expression.
- R32. Multi-statements: chain multiple expressions with colon (:) separator, execute left-to-right sequentially.
- R33. Random number (Ran#): generate pseudo-random decimal in range 0.000 to 0.999.

### Scientific Constants (expanded)
- R34. Expand scientific constants to full Casio 40-constant CODATA set: proton mass, neutron mass, electron mass, muon mass, Bohr radius, Planck constant, nuclear magneton, Bohr magneton, reduced Planck constant, fine-structure constant, classical electron radius, Compton wavelength, proton Compton wavelength, neutron Compton wavelength, Rydberg constant, atomic mass unit, proton/electron/neutron/muon magnetic moments, Faraday constant, elementary charge, Avogadro constant, Boltzmann constant, molar volume, molar gas constant, speed of light, first/second radiation constants, Stefan-Boltzmann constant, electric constant, magnetic constant, magnetic flux quantum, gravitational acceleration, conductance quantum, impedance of vacuum, Celsius temperature, Newtonian gravitational constant, standard atmosphere.

### Metric Conversions (expanded)
- R35. Expand metric conversions to full Casio 40-conversion set: in<->cm, ft<->m, yd<->m, mile<->km, n mile<->m, acre<->m^2, gal(US)<->L, gal(UK)<->L, pc<->km, km/h<->m/s, oz<->g, lb<->kg, atm<->Pa, mmHg<->Pa, hp<->kW, kgf/cm^2<->Pa, kgf*m<->J, lbf/in^2<->kPa, F<->C, J<->cal.

## Success Criteria

- Every function documented in the Casio fx-991MS manual (76 pages) has a working equivalent in CalcMate
- A student can replicate any example calculation from the manual in CalcMate and get the same result
- Existing CalcMate features (graphing, unit converter UI, history, ads) remain unchanged
- All new features have unit tests covering the manual's example calculations

## Scope Boundaries

- Not implementing the physical keypad simulation (SHIFT/ALPHA key modifier system) — CalcMate uses its own touch UI
- Not implementing the exact Casio display format (two-line dot matrix) — CalcMate uses modern Compose UI
- Not replicating Casio-specific limitations (79-step input, 10-level stack)
- Not implementing Casio's replay/copy system — CalcMate already has superior history with Room DB
- Gradian support is a low-priority nice-to-have for Casio parity (rarely used outside France)

## Key Decisions

- **Scope = fx-991MS (top model)**: includes all features from fx-100MS and fx-570MS as subsets
- **UI integration**: new modes (CMPLX, SD, REG, BASE, MAT, VCT) become additional screens or tabs within the existing calculator, not separate apps
- **Backward compatible**: all existing R1-R33 requirements from the original brainstorm remain intact

## Outstanding Questions

### Resolve Before Planning
- None — all product decisions resolved by the Casio manual specification

### Deferred to Planning
- [Affects R9-R11][Technical] How to integrate CMPLX mode UI — separate tab, or inline complex toggle on calculator screen?
- [Affects R14-R20][Technical] SD/REG mode data entry UI — how to design the data table input for touch screens?
- [Affects R21-R23][Technical] BASE mode UI — how to present binary/octal/hex input and conversion on mobile?
- [Affects R26-R27][Technical] MAT/VCT mode — grid input editor design for matrices and vectors on mobile
- [Affects R31][Needs research] CALC mode UX — how to prompt for variable values on mobile (modal dialog vs inline?)
- [Affects R34][Needs research] Verify current CODATA values (Casio uses 2010, should we use 2018 or latest?)

## Next Steps

-> `/ce:plan` for structured implementation planning
