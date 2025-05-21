package ch.sbb.atlas.api.user.administration.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import org.junit.jupiter.api.Test;

class NovaTerminationVotePermissionRestrictionModelTest {

  @Test
  void shouldHaveNullValueAndCorrectType() {
    NovaTerminationVotePermissionRestrictionModel model = new NovaTerminationVotePermissionRestrictionModel();

    assertNull(model.getValue());
    assertEquals("null", model.getValueAsString());
    assertEquals(PermissionRestrictionType.NOVA_TERMINATION_VOTE, model.getType());
  }

  @Test
  void shouldSetValueTrue() {
    NovaTerminationVotePermissionRestrictionModel model = new NovaTerminationVotePermissionRestrictionModel(true);

    assertTrue(model.getValue());
    assertEquals("true", model.getValueAsString());
  }

  @Test
  void shouldSetValueFalse() {
    NovaTerminationVotePermissionRestrictionModel model = new NovaTerminationVotePermissionRestrictionModel();
    model.setValueAsString("false");

    assertFalse(model.getValue());
    assertEquals("false", model.getValueAsString());
  }

}
