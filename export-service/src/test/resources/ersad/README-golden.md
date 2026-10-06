# ERSAD golden NeTEx files

Source: Confluence page 3825765495 ("ERSAD Export aus ATLAS"), the two worked examples embedded
in the spec. Fetched via the Confluence API and adapted in-place; the Confluence page itself was
never edited.

## tavannes-8500100.xml

Single-station example (Tavannes, 8500100) exercising every ERSAD object type: Station, Address,
AccessibilityAssessment, AssistanceService, TicketingEquipment (TICKET_COUNTER Contact Point),
TicketingService (INFORMATION_DESK Contact Point, since handoff plan §8.2), SanitaryEquipment
(Toilet), Quays, Parking + ParkingArea, SitePathLinks.

Adaptations applied vs. the Confluence draft (plan §0.2):

1. `TotalCapacity` removed from the `Parking`/`ParkingArea` elements (not modelled — ATLAS has no
   equivalent field on `ParkingLotVersion`).
2. `ParkingArea` id suffix renamed from the draft's `:prm` to `:parkingArea`.
3. `SitePathLink` ids renamed from the draft's ad hoc `:path:ref1-quay3` / `:path:ref1-toilet` to
   sequential `:pathLink:1` / `:pathLink:2` (with matching nested `:aa` ids), per plan §0.2's
   `:pathLink:<n>` suffix rule.
4. Markdown backslash-escapes (`STOP\_POINT`, `NOT\_APPLICABLE`, ...) introduced by the
   Confluence-to-markdown export were unescaped back to plain underscores in comments.
5. `:aa` suffix used throughout for `AccessibilityAssessment` (Station/Quay/Toilet/ContactPoint/
   PathLink) — see open question below re: plan's parenthetical "(AssistanceAvailability)".

**Caveat (documented in the file's header comment):** per the Confluence spec's own text, the
real ATLAS data for Tavannes does *not* actually have Contact Point / Parking / Reference Point /
Relation rows — those sections in this example are illustrative-only, included to demonstrate the
NeTEx structure. WP3/WP7 should not assume these correspond to real `contact_point_version` /
`parking_lot_version` / `reference_point_version` / `relation_version` rows for service point
8500100; see `fixtures-tavannes.md`.

## burgdorf-yverdon-bellinzona.xml

Three-station example (Burgdorf 8508005, Yverdon-les-Bains 8504200, Bellinzona 8505213) covering
only Station + AccessibilityAssessment + AssistanceService + Toilet + Quays (+ AccessVehicleEquipment
per quay). No Contact Point, Parking, Reference Point or SitePathLink data is present in the source
at all, so **no structural adaptations were needed** — only the same markdown backslash-escape
cleanup as above.

## Open questions (flagged for downstream WPs)

- Plan §0.2 lists the `:aa` suffix with a parenthetical "(AssistanceAvailability)"; the Confluence
  draft consistently uses `:aa` for **AccessibilityAssessment**, not AssistanceService, on every
  station/quay/toilet/contact-point/pathlink. Treated as a likely plan typo; `:aa` was applied to
  AccessibilityAssessment throughout. AssistanceService elements were given a pragmatic `:assistance`
  suffix instead (not explicitly listed in plan §0.2's 5-item suffix list).
- Plan §0.2's `:access` suffix has no obviously corresponding NeTEx sub-element in either example
  (TicketingEquipment? AccessVehicleEquipment?). `AccessVehicleEquipment` was initially given a
  pragmatic `:boarding` suffix; handoff plan §8.4 later renamed it to `:access` everywhere (see
  "Handoff plan §8" section below), so this is now resolved.

## WP6 adaptations (StAX writer `ErsadNetexFileWriter`)

The golden files are the element contract of `ErsadNetexFileWriterTest` (compared ignoring comments
and whitespace). Where the draft contradicted plan §0.2 or had no ERSAD model source, §0.2 / the
model won; every change is also listed in the file's header comment.

Both files:
- Root element declares `xmlns:gml` and `xmlns:siri` (plan WP6 header).

tavannes-8500100.xml:
- Removed StopPlace-level `StepFreeAccess` and the StopPlace AccessibilityAssessment `Comment`
  (no source in `ErsadStation`).
- Removed `TicketingService` (openingHours is free text, not structured DayType/Timeband).
- Removed `NumberOfMachines` (ATLAS only has a boolean).
- Removed `entrances`/`StopPlaceEntrance` (reference points are not in the ERSAD model; they only
  appear as `SitePathLink` `PlaceRef`s).
- `ParkingArea` id is `<parkingSloid>:parkingArea` (`ch:1:sloid:100:parking:1:parkingArea`),
  i.e. §0.2 suffix on the entity's own sloid; the station-level id would collide for several parkings.

burgdorf-yverdon-bellinzona.xml:
- Removed the Burgdorf toilet `Note` (no toilet info field in the model).
- Yverdon/Bellinzona (PRE_REGISTRATION): `MobilityImpairedAccess`/`WheelchairAccess` `true` → `partial` (§0.2).

Element order where the golden files are silent: `Hoist` before `Ramp` (AccessVehicleEquipmentBoardingGroup
XSD order); all `TicketingEquipment` before all `SanitaryEquipment` in `placeEquipments`.

## Handoff plan §8 business decisions (this revision)

- **§8.4** Quay `AccessVehicleEquipment` id suffix renamed `:boarding` → `:access` everywhere.
- **§8.3 (known limitation, no longer exported)** Removed Quay `TactileGuidingStrips` (was sourced
  from platform `contrastingAreas`), PathLink `VisualSignsAvailable` (was sourced from
  `contrastingAreas`), and StopPlace `VisualSignsAvailable` (was sourced from `visualInfo`).
  PathLink `tactileVisualMarks` → `TactileGuidanceAvailable` is kept (see XSD renames below).
- **§8.2** `INFORMATION_DESK` contact points are now exported as a `TicketingService` `LocalService`
  in StopPlace `localServices` (id = the contact point's own sloid), per
  `netex_part_1/part1_ifopt/netex_ifopt_localService_version.xsd` (EquipmentGroup, then
  LocalServiceGroup, then TicketingServiceGroup). `TICKET_COUNTER` contact points are unchanged
  (`TicketingEquipment` in `placeEquipments`). `LocalService` has no `AccessibilityAssessment` or
  `InductionLoops`, so the information desk's accessibility/induction-loop data (if any) cannot be
  represented and is dropped — flagged as a data-loss limitation.
- **§8.1** Platform `boardingDevice` now maps to real `AccessVehicleEquipmentBoardingGroup`
  elements (`netex_framework/netex_reusableComponents/netex_equipmentVehiclePassenger_version.xsd`):
  `Hoist` (`LIFTS`→true, `NO`→false, else omitted) then `Ramp` (`RAMPS`→true, `NO`→false, else
  omitted), written only if `Hoist` or `Ramp` is present. The non-existent `RampAvailable` and
  `BoardingHeightAdjustment` elements were removed. Platform `levelAccessWheelchair` now maps to
  the Quay `AccessibilityAssessment` limitation `LevelAccessIntoVehicle` instead (same
  true/false/omit mapping as the removed `RampAvailable`/`BoardingHeightAdjustment` booleans).

## xmllint schema-validity fixes (`NeTEx_publication.xsd`, v2.0)

Both golden files previously failed `xmllint --noout --schema .../NeTEx_publication.xsd`; both now
report `validates`. Fixes applied to the writers and golden files:

- `HearingLoops` is not a NeTEx element; renamed to `InductionLoops`
  (`TicketingEquipmentAccessibilityGroup`).
- `TactileGuidingStrips` is not a NeTEx element; the one remaining caller (PathLink
  `tactileVisualMarks`) now maps to the `AccessibilityLimitation` limitation
  `TactileGuidanceAvailable`.
- `AccessibilityAssessment` is not allowed inside `TicketingEquipment`
  (Equipment/PassengerEquipment groups have no `AccessibilityAssessment`); replaced with
  `TicketingEquipment`'s own accessibility booleans (`WheelchairSuitable`, `InductionLoops`, in
  `TicketingEquipmentAccessibilityGroup` XSD order) where the source maps cleanly.
- Every `AccessibilityAssessment` now writes the mandatory `MobilityImpairedAccess` before
  `limitations` (`AccessibilityAssessmentGroup`): the `WheelchairAccess` limitation's value when
  present, `"unknown"` otherwise. Likewise `AccessibilityLimitation` now always carries the
  mandatory `WheelchairAccess` field first, synthesized as `"unknown"` when the source has none
  (PathLink's `AccessibilityLimitation` previously had none at all) — **flagged for business
  review**, since `"unknown"` is a literal placeholder, not a real ATLAS value.
- StopPlace child order corrected to the Site/SiteElement/StopPlace XSD group sequence: `Name`,
  `PrivateCode`, `Centroid`, `Url`, `PostalAddress`, `AccessibilityAssessment`, `placeEquipments`,
  `localServices`, `TransportMode`, `StopPlaceType`, `quays`, `pathLinks`.
- `PostalAddress`: `Town` now written before `PostCode` (`PostalAddressGroup` order); `Url`
  belongs at the Place/SiteElement level, not inside `PostalAddress`, and was moved accordingly.
- `SanitaryEquipment`: `AccessibilityAssessment` now written before `SanitaryFacilityList`
  (`SanitaryEquipmentGroup` order).
- `AssistanceService`: fields reordered to `Note`, then `AssistanceFacilityList`, then
  `AssistanceAvailability` (`AssistanceServiceGroup` order).
- `ParkingArea`: `ParkingUserTypes` moved inside a nested `<parkingProperties><ParkingProperties>`
  element (`ParkingPropertiesGroup`), not written directly under `ParkingArea`.
- `SitePathLink` `From`/`To` `PlaceRef`s no longer carry a `version` attribute: `version="1"`
  triggered the NeTEx keyref check against places not present in this delivery (the toilet/ref
  point placeholders), which then failed; omitting `version` on these refs satisfies NeTEx keyref
  semantics for external references.

## WP7 adaptation: `Longitude`/`Latitude` corrected to real `CoordinateTransformer` output

`tavannes-8500100.xml`'s `Longitude`/`Latitude` (and the matching literals in
`ErsadGoldenFixtures.tavannes()`) were changed from the Confluence
draft's literal WGS84 values (`7.20164538962` / `47.21984466031`) to `7.20164539670` /
`47.21984467076`.

Plan §0.2 requires real coordinates to always be routed through
`ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer` (ATLAS stores positions as LV95).
For the Tavannes fixture's LV95 pair (`east=2582049.83754`, `north=1229906.62316`, as seeded in
`ExportErsadNetexIntegrationTest`/`fixtures-tavannes.md`), the transformer's LV95→WGS84 output does
not exactly equal the draft's literal WGS84 values — it differs by ~9e-8° longitude and ~1e-8°
latitude, both of which are visible at the 11-decimal precision `ErsadNetexFileWriter` writes.
Since golden-file fidelity to the real transformer matters more than byte-matching the Confluence
draft (which never round-tripped through the transformer at all), the golden values were updated
to the real transformer output rather than special-casing the writer/test. See item 13 in
`tavannes-8500100.xml`'s header comment.
