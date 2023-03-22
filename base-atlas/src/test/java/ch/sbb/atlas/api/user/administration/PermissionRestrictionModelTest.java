package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class PermissionRestrictionModelTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldDeserializeSboidPermissionRestriction() throws JsonProcessingException {
    String jsonValue = """
        {"value":"ch:1:sboid:1100000","type":"BUSINESS_ORGANISATION"}
        """;
    PermissionRestrictionModel<?> permissionRestrictionModel = objectMapper.readValue(jsonValue,
        PermissionRestrictionModel.class);

    assertThat(permissionRestrictionModel.getType()).isEqualTo(PermissionRestrictionType.BUSINESS_ORGANISATION);
    assertThat(permissionRestrictionModel).isInstanceOf(SboidPermissionRestrictionModel.class);
  }

  @Test
  void shouldDeserializeCantonPermissionRestriction() throws JsonProcessingException {
    String jsonValue = """
        {"value":"BERN","type":"CANTON"}
        """;
    PermissionRestrictionModel<?> permissionRestrictionModel = objectMapper.readValue(jsonValue,
        PermissionRestrictionModel.class);

    assertThat(permissionRestrictionModel.getType()).isEqualTo(PermissionRestrictionType.CANTON);
    assertThat(permissionRestrictionModel).isInstanceOf(CantonPermissionRestrictionModel.class);
  }
}