package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.imports.ItemProcessResponseStatus;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.module.geodata.controller.ServicePointGeoDataApiInternalController;
import ch.sbb.atlas.servicepointdirectory.module.geodata.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.module.geodata.service.GeoReferenceJobService;
import ch.sbb.atlas.servicepointdirectory.module.geodata.service.ServicePointGeoDataService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicePointGeoDataControllerTest {

  @Mock
  private GeoReferenceJobService geoReferenceJobService;

  @Mock
  private ServicePointGeoDataService servicePointGeoDataService;

  private ServicePointGeoDataApiInternalController geoReferenceController;

  @BeforeEach
  void setUp() {
    geoReferenceController = new ServicePointGeoDataApiInternalController(geoReferenceJobService, servicePointGeoDataService);
  }

  @Test
  void shouldCallService() {
    Tile expectedTile = Tile.getDefaultInstance();
    when(servicePointGeoDataService.getGeoData(eq(5), eq(7), eq(10), any(LocalDate.class))).thenReturn(expectedTile);

    Tile tile = geoReferenceController.getServicePointsGeoData(5, 7, 10, Optional.empty());

    assertThat(tile).isEqualTo(expectedTile);
  }

  @Test
  void shouldUpdateSuccessfullyServicePointGeoLocation() {
    //given
    Long id = 1000L;
    String sloid = "ch:1:sloid:7000";
    UpdateGeoLocationResultContainer resultModel = UpdateGeoLocationTestData.getModel();
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