package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;

@IntegrationTest
class PermissionRestrictionModelTest {

  @MockitoBean
  private BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  @MockitoBean
  private TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldDeserializeSboidPermissionRestriction() {
    String jsonValue = """
        {"valueAsString":"ch:1:sboid:1100000","type":"BUSINESS_ORGANISATION"}
        """;
    PermissionRestrictionModel permissionRestrictionModel = objectMapper.readValue(jsonValue,
        PermissionRestrictionModel.class);

    assertThat(permissionRestrictionModel.getType()).isEqualTo(PermissionRestrictionType.BUSINESS_ORGANISATION);
    assertThat(permissionRestrictionModel).isInstanceOf(SboidPermissionRestrictionModel.class);
  }

  @Test
  void shouldDeserializeCantonPermissionRestriction() {
    String jsonValue = """
        {"valueAsString":"BERN","type":"CANTON"}
        """;
    PermissionRestrictionModel permissionRestrictionModel = objectMapper.readValue(jsonValue,
        PermissionRestrictionModel.class);

    assertThat(permissionRestrictionModel.getType()).isEqualTo(PermissionRestrictionType.CANTON);
    assertThat(permissionRestrictionModel).isInstanceOf(CantonPermissionRestrictionModel.class);
  }

  @Test
  void shouldDeserializeCountryPermissionRestriction() {
    String jsonValue = """
        {"valueAsString":"MACEDONIA","type":"COUNTRY"}
        """;
    PermissionRestrictionModel permissionRestrictionModel = objectMapper.readValue(jsonValue,
        PermissionRestrictionModel.class);

    assertThat(permissionRestrictionModel.getType()).isEqualTo(PermissionRestrictionType.COUNTRY);
    assertThat(permissionRestrictionModel).isInstanceOf(CountryPermissionRestrictionModel.class);
  }

  @Test
  void shouldDeserializeBulkImportPermissionRestriction() {
    String jsonValue = """
        {"valueAsString":"true","type":"BULK_IMPORT"}
        """;
    PermissionRestrictionModel permissionRestrictionModel = objectMapper.readValue(jsonValue,
        PermissionRestrictionModel.class);

    assertThat(permissionRestrictionModel.getType()).isEqualTo(PermissionRestrictionType.BULK_IMPORT);
    assertThat(permissionRestrictionModel).isInstanceOf(BulkImportPermissionRestrictionModel.class);
  }
}
