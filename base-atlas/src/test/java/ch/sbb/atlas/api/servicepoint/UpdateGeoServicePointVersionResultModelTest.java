package ch.sbb.atlas.api.servicepoint;

import static ch.sbb.atlas.api.servicepoint.UpdateGeoLocationTesData.getServicePointGeolocationReadModel;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.UpdateGeoServicePointVersionResultModel.VersionDataRage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpdateGeoServicePointVersionResultModelTest {

  @Test
  void shouldGetResponseMessageWithoutVersioning() {
    //when
    UpdateGeoServicePointVersionResultModel resultModel = UpdateGeoLocationTesData.getModel();

    //then
    assertThat(resultModel).isNotNull();
    assertThat(resultModel.isHasMergedVersions()).isFalse();
    assertThat(resultModel.getResponseMessage()).isNotNull();
    assertThat(resultModel.getResponseMessage()).isEqualTo(
        "No versioning changes happened!<br> [SwissMunicipalityNumber=236,SwissMunicipalityName=Bern,SwissLocalityName=Bern] "
            + "differs from [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

  @Test
  void shouldGetResponseMessageWithMerging() {
    //given
    ServicePointGeolocationReadModel currentServicePointGeolocation = getServicePointGeolocationReadModel();
    ServicePointGeolocationReadModel updatedServicePointGeolocation = getServicePointGeolocationReadModel();
    updatedServicePointGeolocation.getSwissLocation()
        .setLocalityMunicipality(
            LocalityMunicipalityModel.builder().municipalityName("Wyleregg").localityName("Wyleregg").fsoNumber(101).build());
    List<VersionDataRage> currentVersionsDataRange = new ArrayList<>();
    currentVersionsDataRange.add(new VersionDataRage(LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31)));
    currentVersionsDataRange.add(new VersionDataRage(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 12, 31)));
    List<VersionDataRage> updatedVersionsDataRange = new ArrayList<>();
    updatedVersionsDataRange.add(new VersionDataRage(LocalDate.of(2000, 1, 1), LocalDate.of(2001, 12, 31)));

    //when
    UpdateGeoServicePointVersionResultModel resultModel = UpdateGeoServicePointVersionResultModel.builder()
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
        "Merged versions: <br>before [UpdateGeoServicePointVersionResultModel.VersionDataRage(validFrom=2000-01-01, "
            + "validTo=2000-12-31), UpdateGeoServicePointVersionResultModel.VersionDataRage(validFrom=2001-01-01, "
            + "validTo=2001-12-31)] <br>after [UpdateGeoServicePointVersionResultModel.VersionDataRage(validFrom=2000-01-01, "
            + "validTo=2001-12-31)][SwissMunicipalityNumber=236,SwissMunicipalityName=Bern,SwissLocalityName=Bern] differs from"
            + " [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

}