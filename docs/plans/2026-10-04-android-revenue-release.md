# CalcMate Android revenue release

## Decision and evidence

Improve the existing Android Scientific Calculator (CalcMate), package `com.perfectappstudio.scientificcalc`. Google Play Console on 4 October 2026 shows 2.97K installed users and 1.24K monthly active devices. AdMob shows estimated earnings of $6.57 last month and $1.82 over its selected last-seven-days interval. These are account dashboard estimates, not a forecast. Reusing this audience, signing identity, and ad account has a lower delivery cost than starting another app.

[HiPER's listing](https://play.google.com/store/apps/details?id=cz.hipercalc&hl=en) demonstrates demand for fractions, graphs, calculus, matrices, and built-in help. It does not prove CalcMate can match its growth or revenue. [Our live listing](https://play.google.com/store/apps/details?id=com.perfectappstudio.scientificcalc&hl=en) currently overstates supported equation solving and understates advertising data practices.

## Product and design

Ship the existing scientific/basic calculator, editable history, copy/share, decimal/fraction display, graphing, supported equation solvers, unit conversions, statistics, numerical calculus, matrices, vectors, Base-N, and searchable constants. Add practical examples and concise help so new users can discover those tools. Retain the existing navy body, readable pale display, grouped keys, amber editing keys, and mint equals key. Use existing Material dialogs and accessibility labels; no onboarding interrupts calculation.

## Monetization

Use the existing production anchored adaptive banner and AdMob app ID; previews use Google's test IDs. Keep core tools available offline and gate ad requests through UMP. Do not introduce full-screen ads during calculation or an unimplemented paid upgrade. Verify production app readiness, published consent messages, seller verification, privacy policy, and Play Data safety before rollout. [UMP guidance](https://developers.google.com/admob/android/privacy) and [SDK disclosure](https://developers.google.com/admob/android/privacy/play-data-disclosure) govern integration checks.

## Delivery gates

1. Review Android changes and retain ongoing iOS work locally.
2. Pass unit tests, release lint, instrumented calculator/calculus/graph/solver/converter flows on the 16 KB Android emulator, and inspect current screenshots.
3. CI builds and signs the next version using the existing Playstore environment and uploads it to internal testing. Preserve the published signing key and application ID.
4. Verify the signed bundle, pre-launch results, production consent, policy disclosures, listing copy, and screenshots; promote a staged production release through Play Console.
5. Report the actual public version, review state, and verified monetization status. A submitted or internal build is not a public release.

## Usage constraint

Starting account weekly usage: 10%. The clarified allowance is 20 percentage points beyond baseline, giving a 30% ceiling. Stop at 29% to allow for reporting delay. Account percentages are rounded and shared across chats, so they cannot prove this chat's exact token consumption. Check at major phases. Do not buy credits or consume reset credits.
