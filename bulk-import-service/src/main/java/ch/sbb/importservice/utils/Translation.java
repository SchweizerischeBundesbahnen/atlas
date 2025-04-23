package ch.sbb.importservice.utils;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Translation {

  @AllArgsConstructor
  @Getter
  public class Values {

    private String de;
    private String fr;
    private String it;
  }

  private final static Map<ApplicationType, Values> applicationTranslations = Map.of(
      ApplicationType.SEPODI, new Values("Dienststellen", "points de services", "posto di servizio"),
      ApplicationType.PRM, new Values("Barrierefreiheit", "accessibilité", "accessibilità"),
      ApplicationType.LIDI, new Values("Linienverzeichnis", "liste des lignes", "elenco delle linee")
  );

  private final static Map<BusinessObjectType, Values> objectTypeTranslations = Map.ofEntries(
      Map.entry(BusinessObjectType.SERVICE_POINT, new Values("Dienststelle", "service", "posto di servizio")),
      Map.entry(BusinessObjectType.TRAFFIC_POINT, new Values("Haltekante", "bordure d'arrêt", "bordo di fermata")),
      Map.entry(BusinessObjectType.LOADING_POINT, new Values("Ladestelle", "places de chargement", "posti di carico")),
      Map.entry(BusinessObjectType.STOP_POINT, new Values("Haltestelle", "l'arrêt", "fermata")),
      Map.entry(BusinessObjectType.CONTACT_POINT, new Values("Schalter", "guichet", "sportello")),
      Map.entry(BusinessObjectType.PARKING_LOT, new Values("Parkplatz", "places de stationnement", "parcheggio")),
      Map.entry(BusinessObjectType.PLATFORM_REDUCED, new Values("Haltekante (reduziert)",
          "Bordures d’arrêt (réduit)", "Bordi fermata (ridotto)")),
      Map.entry(BusinessObjectType.PLATFORM_COMPLETE, new Values("Haltekante (komplett)",
          "Bordures d’arrêt (complet)", "Bordi fermata (completo)")),
      Map.entry(BusinessObjectType.REFERENCE_POINT, new Values("Referenzpunkt",
          "point de référence", "punto di riferimento")),
      Map.entry(BusinessObjectType.RELATION, new Values("Verbindungen", "relations", "collegamenti")),
      Map.entry(BusinessObjectType.TOILET, new Values("Toilette", "toilettes", "servizi igienici")),
      Map.entry(BusinessObjectType.LINE, new Values("Linie", "ligne", "linea"))
  );

  private final static Map<ImportType, Values> importTypeTranslations = Map.of(
      ImportType.UPDATE, new Values("aktualisiert", "mises à jour", "aggiornati"),
      ImportType.CREATE, new Values("erstellt", "créées", "creati"),
      ImportType.TERMINATE, new Values("terminiert", "terminées", "terminati")
  );

  public static Values of(ApplicationType applicationType) {
    return applicationTranslations.get(applicationType);
  }

  public static Values of(BusinessObjectType businessObjectType) {
    return objectTypeTranslations.get(businessObjectType);
  }

  public static Values of(ImportType importType) {
    return importTypeTranslations.get(importType);
  }

}
