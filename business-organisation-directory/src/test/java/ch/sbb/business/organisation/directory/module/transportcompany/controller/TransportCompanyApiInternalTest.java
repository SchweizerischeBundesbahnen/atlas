package ch.sbb.business.organisation.directory.module.transportcompany.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.business.organisation.directory.module.transportcompany.bav.TransportCompanyBavImportService;
import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompany;
import ch.sbb.business.organisation.directory.module.transportcompany.service.TransportCompanyService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class TransportCompanyApiInternalTest extends BaseControllerApiTest {

  @MockitoBean
  private TransportCompanyService transportCompanyService;

  @MockitoBean
  private TransportCompanyBavImportService transportCompanyBavImportService;

  @Nested
  @DisplayName("GET /internal/transport-companies/{id}")
  class GetTransportCompany {

    @BeforeEach
    void setUp() {
      when(transportCompanyService.findById(1L)).thenReturn(Optional.of(transportCompany()));
    }

    @Test
    void shouldGetTransportCompany() throws Exception {
      getTransportCompany();
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetTransportCompanyAsStandardUser() throws Exception {
      getTransportCompany();
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetTransportCompanyAsUnauthorized() throws Exception {
      getTransportCompany();
    }

    private void getTransportCompany() throws Exception {
      mvc.perform(get("/internal/transport-companies/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", is(1)))
          .andExpect(jsonPath("$.number", is("0001")))
          .andExpect(jsonPath("$.abbreviation", is("SBB")));
    }

    @Test
    void shouldReturnNotFoundWhenTransportCompanyDoesNotExist() throws Exception {
      when(transportCompanyService.findById(1L)).thenReturn(Optional.empty());

      mvc.perform(get("/internal/transport-companies/1"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("GET /internal/transport-companies/bySboid")
  class GetTransportCompaniesBySboid {

    @BeforeEach
    void setUp() {
      when(transportCompanyService.findBySboid("ch:1:sboid:1000000")).thenReturn(List.of(transportCompany()));
    }

    @Test
    void shouldGetTransportCompaniesBySboid() throws Exception {
      getTransportCompaniesBySboid();
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetTransportCompaniesBySboidAsStandardUser() throws Exception {
      getTransportCompaniesBySboid();
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetTransportCompaniesBySboidAsUnauthorized() throws Exception {
      getTransportCompaniesBySboid();
    }

    private void getTransportCompaniesBySboid() throws Exception {
      mvc.perform(get("/internal/transport-companies/bySboid").param("sboid", "ch:1:sboid:1000000"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].id", is(1)))
          .andExpect(jsonPath("$[0].number", is("0001")))
          .andExpect(jsonPath("$[0].abbreviation", is("SBB")));
    }
  }

  @Nested
  @DisplayName("POST /internal/transport-companies/loadFromBAV")
  class LoadTransportCompaniesFromBav {

    @Test
    void shouldLoadTransportCompaniesFromBavAsAdmin() throws Exception {
      mvc.perform(post("/internal/transport-companies/loadFromBAV"))
          .andExpect(status().isOk());

      verify(transportCompanyBavImportService).saveTransportCompaniesFromBav();
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotLoadTransportCompaniesFromBavAsStandardUser() throws Exception {
      mvc.perform(post("/internal/transport-companies/loadFromBAV"))
          .andExpect(status().isForbidden());

      verifyNoInteractions(transportCompanyBavImportService);
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotLoadTransportCompaniesFromBavAsUnauthorized() throws Exception {
      mvc.perform(post("/internal/transport-companies/loadFromBAV"))
          .andExpect(status().isForbidden());

      verifyNoInteractions(transportCompanyBavImportService);
    }
  }

  private static TransportCompany transportCompany() {
    return TransportCompany.builder()
        .id(1L)
        .number("0001")
        .abbreviation("SBB")
        .build();
  }

}
