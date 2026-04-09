package ch.sbb.atlas.location.module.geo.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.location.GeoAdminHeightResponse;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.location.module.geo.service.GeoReferenceService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class GeoReferenceApiInternalTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private GeoReferenceService geoReferenceService;

  @Test
  void getLocationInformation() throws Exception {
    // given
    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictNumber(242)
        .swissDistrictName("Biel/Bienne")
        .swissMunicipalityNumber(371)
        .swissMunicipalityName("Biel/Bienne")
        .swissLocalityName("Biel/Bienne")
        .build();
    when(geoReferenceService.getGeoReference(any(CoordinatePair.class))).thenReturn(geoReference);

    // when & then
    mockMvc.perform(get("/internal/geo-reference")
            .param("east", "2585438.0")
            .param("north", "1220155.0")
            .param("spatialReference", "LV95"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.country").value("SWITZERLAND"))
        .andExpect(jsonPath("$.swissCanton").value("BERN"))
        .andExpect(jsonPath("$.swissDistrictNumber").value(242))
        .andExpect(jsonPath("$.swissDistrictName").value("Biel/Bienne"))
        .andExpect(jsonPath("$.swissMunicipalityNumber").value(371))
        .andExpect(jsonPath("$.swissMunicipalityName").value("Biel/Bienne"))
        .andExpect(jsonPath("$.swissLocalityName").value("Biel/Bienne"))
        .andExpect(jsonPath("$.height").doesNotExist());

    verify(geoReferenceService).getGeoReference(any(CoordinatePair.class));
  }

  @Test
  void getLocationInformationWithHeight() throws Exception {
    // given
    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictNumber(242)
        .swissDistrictName("Biel/Bienne")
        .swissMunicipalityNumber(371)
        .swissMunicipalityName("Biel/Bienne")
        .swissLocalityName("Biel/Bienne")
        .height(435.0)
        .build();
    when(geoReferenceService.getGeoReferenceWithHeight(any(CoordinatePair.class))).thenReturn(geoReference);

    // when & then
    mockMvc.perform(get("/internal/geo-reference")
            .param("east", "2585438.0")
            .param("north", "1220155.0")
            .param("spatialReference", "LV95")
            .param("includeHeight", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.country").value("SWITZERLAND"))
        .andExpect(jsonPath("$.swissCanton").value("BERN"))
        .andExpect(jsonPath("$.height").value(435.0));

    verify(geoReferenceService).getGeoReferenceWithHeight(any(CoordinatePair.class));
  }

  @Test
  void getHeight() throws Exception {
    // given
    GeoAdminHeightResponse heightResponse = GeoAdminHeightResponse.builder()
        .height(1201.0)
        .build();
    when(geoReferenceService.getHeight(any(CoordinatePair.class))).thenReturn(heightResponse);

    // when & then
    mockMvc.perform(get("/internal/geo-reference/height")
            .param("east", "2568989.3032")
            .param("north", "1141633.69605")
            .param("spatialReference", "LV95"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.height").value(1201.0));

    verify(geoReferenceService).getHeight(any(CoordinatePair.class));
  }
}