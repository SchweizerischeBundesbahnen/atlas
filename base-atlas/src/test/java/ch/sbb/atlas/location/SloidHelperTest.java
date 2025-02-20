package ch.sbb.atlas.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SloidNotValidException;
import org.junit.jupiter.api.Test;

class SloidHelperTest {

  @Test
  void shouldGetServicePointNumberFromSloid() {
    ServicePointNumber servicePointNumber = SloidHelper.getServicePointNumber("ch:1:sloid:7000");
    assertThat(servicePointNumber.getNumber()).isEqualTo(8507000);
  }

  @Test
  void shouldGetServicePointNumberFromForeignSloid() {
    ServicePointNumber servicePointNumber = SloidHelper.getServicePointNumber("ch:1:sloid:1107000");
    assertThat(servicePointNumber.getNumber()).isEqualTo(1107000);
  }

  @Test
  void shouldReturnBadRequestOnInvalidSloid() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(() -> SloidHelper.getServicePointNumber("ch:1:sloid:a"));
  }

}
