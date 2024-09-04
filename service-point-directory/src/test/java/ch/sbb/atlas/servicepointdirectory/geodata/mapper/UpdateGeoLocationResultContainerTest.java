package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.controller.UpdateGeoLocationTesData;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.UpdateGeoLocationResultContainer.VersionDataRage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpdateGeoLocationResultContainerTest {

  @Test
  void shouldGetResponseMessageWithoutVersioning() {
    //when
    UpdateGeoLocationResultContainer resultModel = UpdateGeoLocationTesData.getModel();

    //then
    assertThat(resultModel).isNotNull();
    assertThat(resultModel.isHasMergedVersions()).isFalse();
    assertThat(resultModel.getResponseMessage()).isNotNull();
    assertThat(resultModel.getResponseMessage()).isEqualTo(
        "No versioning changes happened!<br> [SwissMunicipalityNumber=351,SwissMunicipalityName=Bern,SwissLocalityName=Bern] "
            + "differs from [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

  @Test
  void shouldGetResponseMessageWithMerging() {
    //given
    ServicePointGeolocation currentServicePointGeolocation = UpdateGeoLocationTesData.getModel()
        .getCurrentServicePointGeolocation();
    ServicePointGeolocation updatedServicePointGeolocation = UpdateGeoLocationTesData.getModel()
        .getUpdatedServicePointGeolocation();
    List<VersionDataRage> currentVersionsDataRange = new ArrayList<>();
    currentVersionsDataRange.add(new VersionDataRage(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    currentVersionsDataRange.add(new VersionDataRage(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 12, 31)));
    List<VersionDataRage> updatedVersionsDataRange = new ArrayList<>();
    updatedVersionsDataRange.add(new VersionDataRage(LocalDate.of(2000, 1, 1), LocalDate.of(2001, 12, 31)));

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
    assertThat(resultModel.isHasMergedVersions()).isTrue();
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
    ServicePointGeolocation currentServicePointGeolocation = UpdateGeoLocationTesData.getModel()
        .getCurrentServicePointGeolocation();
    ServicePointGeolocation updatedServicePointGeolocation = UpdateGeoLocationTesData.getModel()
        .getUpdatedServicePointGeolocation();
    List<VersionDataRage> currentVersionsDataRange = new ArrayList<>();
    currentVersionsDataRange.add(new VersionDataRage(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    currentVersionsDataRange.add(new VersionDataRage(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 12, 31)));
    List<VersionDataRage> updatedVersionsDataRange = new ArrayList<>();
    updatedVersionsDataRange.add(new VersionDataRage(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    updatedVersionsDataRange.add(new VersionDataRage(LocalDate.of(2001, 1, 1), LocalDate.of(2000, 6, 1)));
    updatedVersionsDataRange.add(new VersionDataRage(LocalDate.of(2001, 6, 2), LocalDate.of(2000, 12, 31)));

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
    assertThat(resultModel.isHasMergedVersions()).isFalse();
    assertThat(resultModel.isHasAdditionalVersionsGenerated()).isTrue();
    assertThat(resultModel.isHasNumberOfVersionsChanged()).isTrue();
    assertThat(resultModel.getResponseMessage()).isNotNull();
    assertThat(resultModel.getResponseMessage()).isEqualTo(
        "Generated additional versions: <br>before [DataRange(validFrom=2000-01-01 validTo=2000-12-31), DataRange"
            + "(validFrom=2001-01-01 validTo=2001-12-31)] <br>after [DataRange(validFrom=2000-01-01 validTo=2000-12-31), "
            + "DataRange(validFrom=2001-01-01 validTo=2000-06-01), DataRange(validFrom=2001-06-02 "
            + "validTo=2000-12-31)][SwissMunicipalityNumber=351,SwissMunicipalityName=Bern,SwissLocalityName=Bern] differs from"
            + " [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

}