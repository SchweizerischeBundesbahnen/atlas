package ch.sbb.atlas.servicepointdirectory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.BaseImportService;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.versioning.model.Versionable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.ParameterTypes;

import static org.junit.jupiter.api.Assertions.*;

class BaseImportServiceTest {

  private BaseImportServicePointDirectoryService<ServicePointVersion> baseImportServicePointDirectoryService;

  private BaseImportService baseImportService;

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

    baseImportService = new BaseImportService() {
      @Override
      protected void save(Versionable element) {
        //not used
      }

      @Override
      protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder, Versionable servicePointVersion) {
        return itemImportResultBuilder
            .validFrom(servicePointVersion.getValidFrom())
            .validTo(servicePointVersion.getValidTo())
            .status(itemImportResultBuilder.build().getStatus())
            .build();
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

  @Test
  public void testBuildFailedImportResult() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //because method is protected
    Method method = BaseImportService.class.getDeclaredMethod("buildFailedImportResult", Versionable.class, Exception.class);
    method.setAccessible(true);


    Exception mockException = new RuntimeException("Test Exception");
    ServicePointVersion servicePointVersion = ServicePointTestData.createServicePointVersion();
    ItemImportResult result = (ItemImportResult) method.invoke(baseImportService, servicePointVersion, mockException);

    assertNotNull(result);
    assertThat(result.getStatus() == ItemImportResponseStatus.FAILED);
  }

  @Test
  public void testBuildSuccessImportResult() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //because method is protected
    Method method = BaseImportService.class.getDeclaredMethod("buildSuccessImportResult", Versionable.class);
    method.setAccessible(true);


    ServicePointVersion servicePointVersion = ServicePointTestData.createServicePointVersion();
    ItemImportResult result = (ItemImportResult) method.invoke(baseImportService, servicePointVersion);

    assertNotNull(result);
    assertThat(result.getStatus() == ItemImportResponseStatus.SUCCESS);
  }

  @Test
  public void testBuildWarningImportResult() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //because method is protected
    Method method = BaseImportService.class.getDeclaredMethod("buildWarningImportResult", Versionable.class, List.class);
    method.setAccessible(true);


    Exception mockException = new RuntimeException("Test Exception");
    List<Exception> exceptionList = new ArrayList<>();
    exceptionList.add(mockException);

    ServicePointVersion servicePointVersion = ServicePointTestData.createServicePointVersion();
    ItemImportResult result = (ItemImportResult) method.invoke(baseImportService, servicePointVersion, exceptionList);

    assertNotNull(result);
    assertThat(result.getStatus() == ItemImportResponseStatus.WARNING);
  }
}
