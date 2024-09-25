package ch.sbb.importservice.utils;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import org.junit.jupiter.api.Test;

class TranslationTest {

  @Test
  void getLangForApplicationType() {
    assertThat(Translation.getLang(ApplicationType.SEPODI).getDe()).isEqualTo("Dienststellen");
    assertThat(Translation.getLang(ApplicationType.SEPODI).getFr()).isEqualTo("points de services");
    assertThat(Translation.getLang(ApplicationType.SEPODI).getIt()).isEqualTo("posto di servizio");
  }

  @Test
  void getLangForObjectType() {
    assertThat(Translation.getLang(BusinessObjectType.LOADING_POINT).getDe()).isEqualTo("Ladestelle");
    assertThat(Translation.getLang(BusinessObjectType.LOADING_POINT).getFr()).isEqualTo("places de chargement");
    assertThat(Translation.getLang(BusinessObjectType.LOADING_POINT).getIt()).isEqualTo("posti di carico");
  }

  @Test
  void getLangForImportType() {
    assertThat(Translation.getLang(ImportType.CREATE).getDe()).isEqualTo("erstellt");
    assertThat(Translation.getLang(ImportType.CREATE).getFr()).isEqualTo("créées");
    assertThat(Translation.getLang(ImportType.CREATE).getIt()).isEqualTo("creati");
  }
}
