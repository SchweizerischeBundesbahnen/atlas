package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.imports.ItemProcessResponseStatus;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceJobService;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GeoReferenceControllerTest {

  @Mock
  private GeoReferenceService geoReferenceService;

  @Mock
  private GeoReferenceJobService geoReferenceJobService;

  private GeoReferenceController geoReferenceController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    geoReferenceController = new GeoReferenceController(geoReferenceService, geoReferenceJobService);
  }

  @Test
  void shouldCallService() {
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();
    GeoReference geoReference = GeoReference.builder().country(Country.SWITZERLAND).build();
    when(geoReferenceService.getGeoReference(coordinate)).thenReturn(geoReference);

    GeoReference locationInformation = geoReferenceController.getLocationInformation(coordinate);

    assertThat(locationInformation).isEqualTo(geoReference);
    verify(geoReferenceService).getGeoReference(coordinate);
  }

  @Test
  void shouldUpdateSuccessfullyServicePointGeoLocation() {
    //given
    Long id = 1000L;
    String sloid = "ch:1:sloid:7000";
    UpdateGeoLocationResultContainer resultModel = UpdateGeoLocationTesData.getModel();
    when(geoReferenceJobService.updateGeoLocation(id)).thenReturn(resultModel);
    //when
    GeoUpdateItemResultModel result = geoReferenceController.updateServicePointGeoLocation(sloid, id);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(ItemProcessResponseStatus.SUCCESS);
    assertThat(result.getSloid()).isEqualTo(sloid);
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getMessage()).isEqualTo(
        "No versioning changes happened!<br> [SwissMunicipalityNumber=351,SwissMunicipalityName=Bern,SwissLocalityName=Bern] "
            + "differs from [SwissMunicipalityNumber=101,SwissMunicipalityName=Wyleregg,SwissLocalityName=Wyleregg]");
  }

  @Test
  void shouldNotUpdateServicePointGeoLocation() {
    //given
    Long id = 1000L;
    String sloid = "ch:1:sloid:7000";
    when(geoReferenceJobService.updateGeoLocation(id)).thenReturn(null);
    //when
    GeoUpdateItemResultModel result = geoReferenceController.updateServicePointGeoLocation(sloid, id);

    //then
    assertThat(result).isNull();
  }

  @Test
  void shouldNotUpdateServicePointGeoLocationWhenExceptionHappened() {
    //given
    Long id = 1000L;
    String sloid = "ch:1:sloid:7000";
    doThrow(new IllegalStateException("Exception")).when(geoReferenceJobService).updateGeoLocation(any());
    //when
    GeoUpdateItemResultModel result = geoReferenceController.updateServicePointGeoLocation(sloid, id);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(ItemProcessResponseStatus.FAILED);
    assertThat(result.getSloid()).isEqualTo(sloid);
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getMessage()).isEqualTo("Exception");
  }

}