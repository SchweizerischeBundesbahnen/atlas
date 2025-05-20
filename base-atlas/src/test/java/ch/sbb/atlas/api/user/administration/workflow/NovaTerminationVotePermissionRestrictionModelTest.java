package ch.sbb.atlas.api.user.administration.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
