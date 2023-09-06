package ch.sbb.exportservice.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.entity.ServicePointVersion.ServicePointVersionBuilder;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePointVersionRowMapperTest {

  private final ServicePointVersionRowMapper rowMapper = new ServicePointVersionRowMapper();

  @Test
  void shouldMapCategoriesCorrectly() {
    ServicePointVersionBuilder<?, ?> builder = ServicePointVersion.builder();
    rowMapper.setCategories(builder, "BILLETING_MACHINE|BILLETING_MACHINE");

    ServicePointVersion servicePointVersion = builder.build();
    assertThat(servicePointVersion.getCategories()).containsExactlyInAnyOrderElementsOf(Set.of(Category.BILLETING_MACHINE));
    assertThat(servicePointVersion.getCategoriesPipeList()).isEqualTo("BILLETING_MACHINE");
  }

  @Test
  void shouldMapMultipleCategoriesCorrectly() {
    ServicePointVersionBuilder<?, ?> builder = ServicePointVersion.builder();
    rowMapper.setCategories(builder, "BILLETING_MACHINE|PARK_AND_RAIL");

    ServicePointVersion servicePointVersion = builder.build();
    assertThat(servicePointVersion.getCategories()).containsExactlyInAnyOrderElementsOf(Set.of(Category.BILLETING_MACHINE,
        Category.PARK_AND_RAIL));
    assertThat(servicePointVersion.getCategoriesPipeList()).isEqualTo("BILLETING_MACHINE|PARK_AND_RAIL");
  }

  @Test
  void shouldMapMeansOfTransportCorrectly() {
    ServicePointVersionBuilder<?, ?> builder = ServicePointVersion.builder();
    rowMapper.setMeansOfTransport(builder, "BUS|BUS");

    ServicePointVersion servicePointVersion = builder.build();
    assertThat(servicePointVersion.getMeansOfTransport()).containsExactlyInAnyOrderElementsOf(Set.of(MeanOfTransport.BUS));
    assertThat(servicePointVersion.getMeansOfTransportPipeList()).isEqualTo("BUS");
  }

  @Test
  void shouldMapMultipleMeansOfTransportCorrectly() {
    ServicePointVersionBuilder<?, ?> builder = ServicePointVersion.builder();
    rowMapper.setMeansOfTransport(builder, "TRAIN|TRAIN|BUS");

    ServicePointVersion servicePointVersion = builder.build();
    assertThat(servicePointVersion.getMeansOfTransport()).containsExactlyInAnyOrderElementsOf(Set.of(MeanOfTransport.BUS,
        MeanOfTransport.TRAIN));
    assertThat(servicePointVersion.getMeansOfTransportPipeList()).isEqualTo("BUS|TRAIN");
  }

}
