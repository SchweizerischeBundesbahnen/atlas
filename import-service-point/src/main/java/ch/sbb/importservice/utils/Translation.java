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
  public class Lang {

    private String de;
    private String fr;
    private String it;
  }

  private final static Map<ApplicationType, Lang> applicationTranslations = Map.of(
      ApplicationType.SEPODI, new Lang("Dienststellen", "points de services", "posto di servizio"),
      ApplicationType.PRM, new Lang("Barrierefreiheit", "accessibilité", "accessibilità")
  );

  private final static Map<BusinessObjectType, Lang> objectTypeTranslations = Map.of(
      BusinessObjectType.SERVICE_POINT, new Lang("Dienststelle", "service", "posto di servizio"),
      BusinessObjectType.TRAFFIC_POINT, new Lang("Haltekante", "bordure d'arrêt", "bordo di fermata"),
      BusinessObjectType.LOADING_POINT, new Lang("Ladestelle", "places de chargement", "posti di carico"),
      BusinessObjectType.STOP_POINT, new Lang("Haltestelle", "l'arrêt", "fermata"),
      BusinessObjectType.CONTACT_POINT, new Lang("Schalter", "guichet", "sportello"),
      BusinessObjectType.PARKING_LOT, new Lang("Parkplatz", "places de stationnement", "parcheggio"),
      BusinessObjectType.PLATFORM, new Lang("Haltekante", "bordure d'arrêt", "bordo di fermata"),
      BusinessObjectType.REFERENCE_POINT, new Lang("Referenzpunkt", "point de référence", "punto di riferimento"),
      BusinessObjectType.RELATION, new Lang("Verbindungen", "relations", "collegamenti"),
      BusinessObjectType.TOILET, new Lang("Toilette", "toilettes", "servizi igienici")
  );

  private final static Map<ImportType, Lang> importTypeTranslations = Map.of(
      ImportType.UPDATE, new Lang("aktualisiert", "mises à jour", "aggiornati"),
      ImportType.CREATE, new Lang("erstellt", "créées", "creati"),
      ImportType.TERMINATE, new Lang("terminiert", "terminées", "terminati")
  );

  public static Lang getLang(ApplicationType applicationType) {
    return applicationTranslations.get(applicationType);
  }

  public static Lang getLang(BusinessObjectType businessObjectType) {
    return objectTypeTranslations.get(businessObjectType);
  }

  public static Lang getLang(ImportType importType) {
    return importTypeTranslations.get(importType);
  }

}
