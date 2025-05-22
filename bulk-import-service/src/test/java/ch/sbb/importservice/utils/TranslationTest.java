package ch.sbb.importservice.utils;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import org.junit.jupiter.api.Test;

class TranslationTest {

  @Test
  void getForApplicationType() {
    assertThat(Translation.of(ApplicationType.SEPODI).getDe()).isEqualTo("Dienststellen");
    assertThat(Translation.of(ApplicationType.SEPODI).getFr()).isEqualTo("points de services");
    assertThat(Translation.of(ApplicationType.SEPODI).getIt()).isEqualTo("posto di servizio");
  }

  @Test
  void getForObjectType() {
    assertThat(Translation.of(BusinessObjectType.LOADING_POINT).getDe()).isEqualTo("Ladestelle");
    assertThat(Translation.of(BusinessObjectType.LOADING_POINT).getFr()).isEqualTo("places de chargement");
    assertThat(Translation.of(BusinessObjectType.LOADING_POINT).getIt()).isEqualTo("posti di carico");
  }

  @Test
  void getForImportType() {
    assertThat(Translation.of(ImportType.CREATE).getDe()).isEqualTo("erstellt");
    assertThat(Translation.of(ImportType.CREATE).getFr()).isEqualTo("créées");
    assertThat(Translation.of(ImportType.CREATE).getIt()).isEqualTo("creati");
  }
}
