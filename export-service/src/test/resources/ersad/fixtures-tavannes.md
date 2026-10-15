# Fixtures required to reproduce tavannes-8500100.xml

For WP3 (mapper/service) and WP7 (integration tests) to reproduce the Tavannes golden file
(`ch:1:sloid:8500100` / didok 8500100), the following rows are needed. Sloids/ids below are
illustrative placeholders following the golden file's numbering; substitute real generated sloids
as needed.

| Table | Row(s) needed | Notes |
|---|---|---|
| `service_point_version` | 1 row, `number=8500100`, `designationOfficial=Tavannes` | Station root. |
| `service_point_version_geolocation` | 1 row for 8500100 | WGS84 lon/lat as in the golden file's `Centroid`. |
| `stop_point_version` | 1 row, `sloid=ch:1:sloid:8500100`, `address=...` | Backs `ErsadStation`/`ErsadAddress`; accessibility fields (`meansOfTransport`, wheelchair fields) feed `AccessibilityAssessment`/`NetexTriState`. |
| `platform_version` | N rows (1 per Quay in the golden file), each with a `traffic_point_element_version` row for `designation`, boarding device, level access, superelevation, shuttle, vehicle access fields | Backs `ErsadQuay`. Note: `contrasting_areas` is no longer exported (known limitation, handoff plan §8.3) and no longer needs seeding for this golden file. |
| `contact_point_version` | **Illustrative only — see caveat below.** If seeded, 2 rows (one `TICKET_COUNTER`, one `INFORMATION_DESK`) with `sloid`, `type`, `designation`, `openingHours`, wheelchair/induction-loop fields. | Backs `ErsadContactPoint`: `TICKET_COUNTER` → `TicketingEquipment` in `placeEquipments`; `INFORMATION_DESK` → `TicketingService` in `localServices` (handoff plan §8.2). |
| `toilet_version` | 1 row, `sloid`, `designation`, wheelchair field | Backs `ErsadToilet` (SanitaryEquipment). |
| `parking_lot_version` | **Illustrative only — see caveat below.** If seeded, 1 row with `sloid`, `designation`, `placesAvailable`, `prmPlacesAvailable`. | Backs `ErsadParking` (Parking/ParkingArea, minus `TotalCapacity` which is not modelled). |
| `reference_point_version` | **Illustrative only — see caveat below.** If seeded, rows for the 2 StopPlaceEntrance/reference points referenced by the path links. | Feeds `ErsadPathLink.referencePointSloid`/`targetType`. |
| `relation_version` | **Illustrative only — see caveat below.** 2 rows (`:pathLink:1`, `:pathLink:2`), each with `sloid`, `referencePointSloid`, step-free-access, tactile/visual marks fields. | Backs `ErsadPathLink`; `targetSloid` = `relation_version.sloid`, per the `relation_sloids_unique(sloid, reference_point_sloid)` constraint in `prm-schema.sql`. `contrasting_areas` is no longer exported (known limitation, handoff plan §8.3); `tactile_visual_marks` now maps to the `TactileGuidanceAvailable` limitation. |

## Important caveat

Per the Confluence spec's own text, Tavannes' real ATLAS data does **not** actually have Contact
Point / Parking Lot / Reference Point / Relation rows today — the corresponding sections in
`tavannes-8500100.xml` (TicketingEquipment, Parking/ParkingArea, StopPlaceEntrance, SitePathLinks)
are fabricated/illustrative example data included by the spec author purely to demonstrate the
NeTEx structure for those object types, not real values for service point 8500100.

WP3/WP7 have two options:
1. Seed literal fixture rows matching the illustrative XML values (so the golden file byte-for-byte
   round-trips), accepting these are not "real" Tavannes data, or
2. Pick a different real ATLAS station that genuinely has Contact Point/Parking/Reference
   Point/Relation data for those object-type assertions, and keep Tavannes only for the
   Station/Toilet/Quay assertions this golden file's real data actually supports.

This decision is left open for WP3/WP7 and is documented here so it is not mistaken for an
oversight.
