package ch.sbb.atlas.servicepointdirectory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BaseImportServicePointDirectoryServiceTest {

  private BaseImportServicePointDirectoryService<ServicePointVersion> baseImportServicePointDirectoryService;

  @BeforeEach
  void setUp() {
    baseImportServicePointDirectoryService = new BaseImportServicePointDirectoryService<>() {
      @Override
      protected void save(ServicePointVersion element) {
        // not used
      }

      @Override
      protected String[] getIgnoredPropertiesWithoutGeolocation() {
        return new String[]{
            Fields.id,
            Fields.validFrom,
            Fields.validTo
        };
      }

      @Override
      protected String[] getIgnoredPropertiesWithGeolocation() {
        return ArrayUtils.add(getIgnoredPropertiesWithoutGeolocation(), Fields.servicePointGeolocation);
      }

      @Override
      protected String getIgnoredReferenceFieldOnGeolocationEntity() {
        return ServicePointGeolocation.Fields.servicePointVersion;
      }

      @Override
      protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder,
          ServicePointVersion element) {
        return null;
      }
    };
  }

  @Test
  void shouldCopyPropertiesFromCsvVersionToDbVersionWithNewGeolocation() {
    // given
    final ServicePointVersion csvVersion = ServicePointTestData.getBernWyleregg();
    final ServicePointVersion dbVersion = ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation();

    // when
    baseImportServicePointDirectoryService.copyPropertiesFromCsvVersionToDbVersion(csvVersion, dbVersion);

    // then
    assertThat(dbVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
    assertThat(dbVersion.getNumberShort()).isEqualTo(89008);
    assertThat(dbVersion.getServicePointGeolocation().getServicePointVersion()).isEqualTo(dbVersion);
    assertThat(dbVersion.getServicePointGeolocation().getSwissMunicipalityNumber()).isEqualTo(351);
  }

  @Test
  void shouldCopyPropertiesFromCsvVersionToDbVersionWithUpdateGeolocation() {
    // given
    final ServicePointVersion csvVersion = ServicePointTestData.getBernWyleregg();
    final ServicePointVersion dbVersion = ServicePointTestData.getBernWyleregg();
    csvVersion.setDesignationOfficial(null);
    csvVersion.getServicePointGeolocation().setHeight(null);

    dbVersion.setNumberShort(5);
    dbVersion.getServicePointGeolocation().setSwissMunicipalityNumber(5);

    // when
    baseImportServicePointDirectoryService.copyPropertiesFromCsvVersionToDbVersion(csvVersion, dbVersion);

    // then
    assertThat(dbVersion.getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
    assertThat(dbVersion.getNumberShort()).isEqualTo(89008);
    assertThat(dbVersion.getServicePointGeolocation().getServicePointVersion()).isEqualTo(dbVersion);
    assertThat(dbVersion.getServicePointGeolocation().getSwissMunicipalityNumber()).isEqualTo(351);
    assertThat(dbVersion.getServicePointGeolocation().getHeight()).isEqualTo(555);
  }
}
