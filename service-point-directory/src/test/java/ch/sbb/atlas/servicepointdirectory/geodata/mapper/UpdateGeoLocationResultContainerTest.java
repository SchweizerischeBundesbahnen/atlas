package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.controller.UpdateGeoLocationTestData;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.model.UpdateGeoLocationResultContainer.VersionDataRange;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpdateGeoLocationResultContainerTest {

  @Test
  void shouldGetResponseMessageWithoutVersioning() {
    //when
    UpdateGeoLocationResultContainer resultModel = UpdateGeoLocationTestData.getModel();

    //then
    assertThat(resultModel).isNotNull();
    assertThat(resultModel.hasMergedVersions()).isFalse();
    assertThat(resultModel.getResponseMessage()).isNotNull();
    assertThat(resultModel.getResponseMessage()).isEqualTo(
        "No versioning changes happened!<br> [SwissMunicipalityNumber=351,SwissMunicipalityName=Bern,SwissLocalityName=Bern] "
            + "differs from [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

  @Test
  void shouldGetResponseMessageWithMerging() {
    //given
    ServicePointGeolocation currentServicePointGeolocation = UpdateGeoLocationTestData.getModel()
        .getCurrentServicePointGeolocation();
    ServicePointGeolocation updatedServicePointGeolocation = UpdateGeoLocationTestData.getModel()
        .getUpdatedServicePointGeolocation();
    List<VersionDataRange> currentVersionsDataRange = new ArrayList<>();
    currentVersionsDataRange.add(new VersionDataRange(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    currentVersionsDataRange.add(new VersionDataRange(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 12, 31)));
    List<VersionDataRange> updatedVersionsDataRange = new ArrayList<>();
    updatedVersionsDataRange.add(new VersionDataRange(LocalDate.of(2000, 1, 1), LocalDate.of(2001, 12, 31)));

    //when
    UpdateGeoLocationResultContainer resultModel =
        UpdateGeoLocationResultContainer.builder()
            .currentServicePointGeolocation(currentServicePointGeolocation)
            .updatedServicePointGeolocation(updatedServicePointGeolocation)
            .sloid("ch:1:sloid:7000")
            .id(1000L)
            .currentVersionsDataRange(currentVersionsDataRange)
            .updatedVersionsDataRange(updatedVersionsDataRange)
            .build();

    //then
    assertThat(resultModel).isNotNull();
    assertThat(resultModel.hasMergedVersions()).isTrue();
    assertThat(resultModel.getResponseMessage()).isNotNull();
    assertThat(resultModel.getResponseMessage()).isEqualTo(
        "Merged versions: <br>before [DataRange(validFrom=2000-01-01 validTo=2000-12-31), DataRange"
            + "(validFrom=2001-01-01 validTo=2001-12-31)] <br>after [DataRange(validFrom=2000-01-01 validTo=2001-12-31)"
            + "][SwissMunicipalityNumber=351,SwissMunicipalityName=Bern,SwissLocalityName=Bern] differs from "
            + "[SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

  @Test
  void shouldGetResponseMessageWithNewVersions() {
    //given
    ServicePointGeolocation currentServicePointGeolocation = UpdateGeoLocationTestData.getModel()
        .getCurrentServicePointGeolocation();
    ServicePointGeolocation updatedServicePointGeolocation = UpdateGeoLocationTestData.getModel()
        .getUpdatedServicePointGeolocation();
    List<VersionDataRange> currentVersionsDataRange = new ArrayList<>();
    currentVersionsDataRange.add(new VersionDataRange(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    currentVersionsDataRange.add(new VersionDataRange(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 12, 31)));
    List<VersionDataRange> updatedVersionsDataRange = new ArrayList<>();
    updatedVersionsDataRange.add(new VersionDataRange(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    updatedVersionsDataRange.add(new VersionDataRange(LocalDate.of(2001, 1, 1), LocalDate.of(2000, 6, 1)));
    updatedVersionsDataRange.add(new VersionDataRange(LocalDate.of(2001, 6, 2), LocalDate.of(2000, 12, 31)));

    //when
    UpdateGeoLocationResultContainer resultModel =
        UpdateGeoLocationResultContainer.builder()
            .currentServicePointGeolocation(currentServicePointGeolocation)
            .updatedServicePointGeolocation(updatedServicePointGeolocation)
            .sloid("ch:1:sloid:7000")
            .id(1000L)
            .currentVersionsDataRange(currentVersionsDataRange)
            .updatedVersionsDataRange(updatedVersionsDataRange)
            .build();

    //then
    assertThat(resultModel).isNotNull();
    assertThat(resultModel.hasMergedVersions()).isFalse();
    assertThat(resultModel.hasAdditionalVersionsGenerated()).isTrue();
    assertThat(resultModel.hasNumberOfVersionsChanged()).isTrue();
    assertThat(resultModel.getResponseMessage()).isNotNull();
    assertThat(resultModel.getResponseMessage()).isEqualTo(
        "Generated additional versions: <br>before [DataRange(validFrom=2000-01-01 validTo=2000-12-31), DataRange"
            + "(validFrom=2001-01-01 validTo=2001-12-31)] <br>after [DataRange(validFrom=2000-01-01 validTo=2000-12-31), "
            + "DataRange(validFrom=2001-01-01 validTo=2000-06-01), DataRange(validFrom=2001-06-02 "
            + "validTo=2000-12-31)][SwissMunicipalityNumber=351,SwissMunicipalityName=Bern,SwissLocalityName=Bern] differs from"
            + " [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

}