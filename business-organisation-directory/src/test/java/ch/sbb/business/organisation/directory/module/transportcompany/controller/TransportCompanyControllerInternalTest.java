package ch.sbb.business.organisation.directory.module.transportcompany.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.business.organisation.directory.module.transportcompany.bav.TransportCompanyBavImportService;
import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompany;
import ch.sbb.business.organisation.directory.module.transportcompany.service.TransportCompanyService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransportCompanyControllerInternalTest {

  @Mock
  private TransportCompanyService transportCompanyService;

  @Mock
  private TransportCompanyBavImportService transportCompanyBavImportService;

  private TransportCompanyControllerInternal transportCompanyControllerInternal;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transportCompanyControllerInternal = new TransportCompanyControllerInternal(
        transportCompanyService, transportCompanyBavImportService);
  }

  @Test
  void shouldGetTransportCompany() {
    TransportCompany transportCompany = TransportCompany.builder()
        .id(1L)
        .number("0001")
        .abbreviation("SBB")
        .build();
    when(transportCompanyService.findById(1L)).thenReturn(Optional.of(transportCompany));

    TransportCompanyModel result = transportCompanyControllerInternal.getTransportCompany(1L);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getNumber()).isEqualTo("0001");
    assertThat(result.getAbbreviation()).isEqualTo("SBB");
  }

  @Test
  void shouldThrowWhenTransportCompanyDoesNotExist() {
    when(transportCompanyService.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> transportCompanyControllerInternal.getTransportCompany(1L))
        .isInstanceOf(IdNotFoundException.class);
  }

}
