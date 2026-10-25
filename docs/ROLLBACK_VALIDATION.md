# Carrying and ammunition verification (#100)

Verified 2026-10-25 on main `0a54b9df067a4fa7a2dd8963ff4d11ed4ac519a2`,
Google Chrome 155.0.8059.86 on macOS, using `./gradlew runWeb`.
All four acceptance criteria of [#100](https://github.com/jlogicgames/edgard_in_kimeria/issues/100)
are verified for the recorded scenarios.

| Check | Observed result |
| --- | --- |
| Actual enemy death after stone pickup, put-down and throw | Empty hand after respawn; stone restored to authored position; no visible duplicate; stock remains 3/3. |
| Actual death after a fireball | Stock restores from 2/3 to room-entry 3/3; no old projectile remains visible. |
| Actual death after refill 121 | Pickup raises 2/3 to 3/3; death restores entry 2/3 and the world refill. |
| Restart Level via Pause | Canopy stock reduced to 1/3 returns to the first room with level-entry 3/3 and authored tools. Also passed while carrying, after throwing and after putting down. |
| Exit to Menu → New Game | Checked separately with held, thrown and placed stone at 2/3; fresh first-level state, empty hand and 3/3. |
| Exit to Menu → Continue; reload → Continue | Saved second level starts at its entrance with empty hand, 3/3 and refill present; stock spent to 1/3 does not persist. |
| Repeated retries and collection returns | Existing tests verify stock/identity rollback, one live carryable per authored ID and no refill farming. |

The six focused suites `DirectionalActionsTest`, `RoomTest`, `RoomReplayTest`,
`CampaignFlowTest`, `ArtifactInventoryTest` and `SavedProgressTest` passed:
**95 tests, zero failures/errors/skips**. Browser checks used actual controls,
menu actions and lethal contact rather than direct calls to room reset methods.

Setup assistance: F6 advanced to the canopy; F2 helped approach the refill and
was disabled before the recorded pickup/death. F4 prepared a second-level save.
This is a functional smoke, not an unassisted full playthrough or physical-device
acceptance (#101). Temporary screenshots and console dumps were removed at the
author's request; the observed results are retained here and in #100.

A separate development F4 transition crash was reproduced with empty/occupied
hands and full/partial stock. The reproduction, console stack and source cause
are tracked in [#170](https://github.com/jlogicgames/edgard_in_kimeria/issues/170).
Normal menu Continue passed; release transition failure was not established.
