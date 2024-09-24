package ch.sbb.atlas.servicepointdirectory.mapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CreateServicePointMapperTest {

  @Mock
  private ServicePointService servicePointService;
  @Mock
  private LocationService locationService;

  private CreateServicePointMapper createServicePointMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    createServicePointMapper = new CreateServicePointMapper(locationService, servicePointService);
  }

  @Test
  void shouldReturnConflictExceptionWhenNumberAlreadyUsed() {
    when(servicePointService.isServicePointNumberExisting(any())).thenReturn(true);
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.JAPAN)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    assertThrows(ServicePointNumberAlreadyExistsException.class,
        () -> createServicePointMapper.toEntity(servicePointVersionModel));
  }
}
