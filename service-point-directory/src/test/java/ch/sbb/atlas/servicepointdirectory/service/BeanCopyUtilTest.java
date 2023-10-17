package ch.sbb.atlas.servicepointdirectory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.util.BeanCopyUtil;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.junit.jupiter.api.Test;

class BeanCopyUtilTest {

  @Test
  void shouldCopyNonNullProperties() {
    //given
    ServicePointVersion source = ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation();
    source.setId(10L);
    source.setAbbreviation("source abbreviation");
    source.setDesignationOfficial(null);

    ServicePointVersion destination = ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation();
    destination.setId(20L);
    destination.setAbbreviation("dest abbreviation");
    destination.setDesignationOfficial("dest designationOfficial");

    // when
    BeanCopyUtil.copyNonNullProperties(source, destination, "id");

    //then
    assertThat(destination.getId()).isEqualTo(20L);
    assertThat(destination.getAbbreviation()).isEqualTo("source abbreviation");
    assertThat(destination.getDesignationOfficial()).isEqualTo("dest designationOfficial");
  }

}
