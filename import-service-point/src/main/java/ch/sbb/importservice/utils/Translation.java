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
      ApplicationType.PRM, new Values("Barrierefreiheit", "accessibilité", "accessibilità")
  );

  private final static Map<BusinessObjectType, Values> objectTypeTranslations = Map.of(
      BusinessObjectType.SERVICE_POINT, new Values("Dienststelle", "service", "posto di servizio"),
      BusinessObjectType.TRAFFIC_POINT, new Values("Haltekante", "bordure d'arrêt", "bordo di fermata"),
      BusinessObjectType.LOADING_POINT, new Values("Ladestelle", "places de chargement", "posti di carico"),
      BusinessObjectType.STOP_POINT, new Values("Haltestelle", "l'arrêt", "fermata"),
      BusinessObjectType.CONTACT_POINT, new Values("Schalter", "guichet", "sportello"),
      BusinessObjectType.PARKING_LOT, new Values("Parkplatz", "places de stationnement", "parcheggio"),
      BusinessObjectType.PLATFORM, new Values("Haltekante", "bordure d'arrêt", "bordo di fermata"),
      BusinessObjectType.REFERENCE_POINT, new Values("Referenzpunkt", "point de référence", "punto di riferimento"),
      BusinessObjectType.RELATION, new Values("Verbindungen", "relations", "collegamenti"),
      BusinessObjectType.TOILET, new Values("Toilette", "toilettes", "servizi igienici")
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
