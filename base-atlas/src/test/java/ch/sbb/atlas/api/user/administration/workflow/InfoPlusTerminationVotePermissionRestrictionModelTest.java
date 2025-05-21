package ch.sbb.atlas.api.user.administration.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import org.junit.jupiter.api.Test;

class InfoPlusTerminationVotePermissionRestrictionModelTest {

  @Test
  void shouldHaveNullValueAndCorrectType() {
    InfoPlusTerminationVotePermissionRestrictionModel model = new InfoPlusTerminationVotePermissionRestrictionModel();

    assertNull(model.getValue());
    assertEquals("null", model.getValueAsString());
    assertEquals(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE, model.getType());
  }

  @Test
  void shouldSetValueTrue() {
    InfoPlusTerminationVotePermissionRestrictionModel model = new InfoPlusTerminationVotePermissionRestrictionModel(true);

    assertTrue(model.getValue());
    assertEquals("true", model.getValueAsString());
  }

  @Test
  void shouldSetValueFalse() {
    InfoPlusTerminationVotePermissionRestrictionModel model = new InfoPlusTerminationVotePermissionRestrictionModel();
    model.setValueAsString("false");

    assertFalse(model.getValue());
    assertEquals("false", model.getValueAsString());
  }
}
