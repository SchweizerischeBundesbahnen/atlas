package ch.sbb.prm.directory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;

class SloidTest {

  @Test
  void shouldGetServicePointNumberFromSloid() {
    ServicePointNumber servicePointNumber = new Sloid("ch:1:sloid:7000").getServicePointNumber();
    assertThat(servicePointNumber.getNumber()).isEqualTo(8507000);
  }

  @Test
  void shouldGetServicePointNumberFromForeignSloid() {
    ServicePointNumber servicePointNumber = new Sloid("ch:1:sloid:1107000").getServicePointNumber();
    assertThat(servicePointNumber.getNumber()).isEqualTo(1107000);
  }
}