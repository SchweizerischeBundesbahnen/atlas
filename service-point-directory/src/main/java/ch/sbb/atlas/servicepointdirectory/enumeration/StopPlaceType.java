package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum StopPlaceType {

    ORDERLY("Ordentliche Haltestelle", "Arrêt ordinaire", "Fermata ordinaria", null, "Ho", "Ao", "Fo", null),
    ON_REQUEST("Bedarfshaltestelle", "Arrêt sur demande", "Fermata facoltativa", null, "Hb", "Ad", "Ff", null),
    ZONE_ON_REQUEST("Bedarfshaltestelle (Gebiet)", "Arrêt sur demande (zone)", "Fermata facoltativa (zona)", null, "Hg", "Ads", "Ffz", null),
    TEMPORARY("Temporäre Haltestelle", "Arrêt temporaire", "Fermata temporanea", null, "Ht", "At", "Ft", null),
    OUT_OF_ORDER("Haltestelle ausser Betrieb", "Arrêt hors service", "Fermata fuori servizio", null, "Ha", "Ahs", "Ffs", null),
    UNKNOWN("Nicht spezifiziert", "Non spécifié", "Non specificata", null, "Hns", "Ans", "Fns", null),

    ;

    private final String descriptionDe;
    private final String descriptionFr;
    private final String descriptionIt;
    private final String descriptionEn;

    private final String abbreviationDe;
    private final String abbreviationFr;
    private final String abbreviationIt;
    private final String abbreviationEn;
}
