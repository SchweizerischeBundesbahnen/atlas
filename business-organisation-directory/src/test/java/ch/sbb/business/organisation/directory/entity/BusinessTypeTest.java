package ch.sbb.business.organisation.directory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BusinessTypeTest {

  @Test
  public void shouldReturnEmptyString() {
    //given
    //when
    String result = BusinessType.getBusinessTypesPiped(Collections.emptySet());
    //then
    assertThat(result).isEqualTo("");
  }

  @Test
  public void shouldReturnPipedString() {
    //given
    //when
    String result = BusinessType.getBusinessTypesPiped(Set.of(BusinessType.AIR, BusinessType.SHIP));
    //then
    assertThat(result).isEqualTo("20|45");
  }
}