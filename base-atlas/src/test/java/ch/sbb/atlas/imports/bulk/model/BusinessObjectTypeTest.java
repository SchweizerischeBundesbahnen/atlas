package ch.sbb.atlas.imports.bulk.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BusinessObjectTypeTest {

  @Test
  void shouldOfferGlobalIdAsSepodiBusinessObject() {
    assertThat(BusinessObjectType.SEPODI_BUSINESS_OBJECTS).containsExactlyInAnyOrder(
        BusinessObjectType.SERVICE_POINT,
        BusinessObjectType.SERVICE_POINT_GLOBAL_ID,
        BusinessObjectType.TRAFFIC_POINT,
        BusinessObjectType.LOADING_POINT,
        BusinessObjectType.SECTOR,
        BusinessObjectType.SECTOR_GROUP);
  }

  @Test
  void shouldNotOfferGlobalIdOutsideOfSepodi() {
    assertThat(BusinessObjectType.PRM_BUSINESS_OBJECTS).doesNotContain(BusinessObjectType.SERVICE_POINT_GLOBAL_ID);
    assertThat(BusinessObjectType.LIDI_BUSINESS_OBJECTS).doesNotContain(BusinessObjectType.SERVICE_POINT_GLOBAL_ID);
  }

  @Test
  void shouldAssignEveryObjectTypeToExactlyOneApplication() {
    // Given
    Set<BusinessObjectType> assigned = new HashSet<>(BusinessObjectType.SEPODI_BUSINESS_OBJECTS);
    assigned.addAll(BusinessObjectType.PRM_BUSINESS_OBJECTS);
    assigned.addAll(BusinessObjectType.LIDI_BUSINESS_OBJECTS);

    // When
    int totalAssignments = BusinessObjectType.SEPODI_BUSINESS_OBJECTS.size()
        + BusinessObjectType.PRM_BUSINESS_OBJECTS.size()
        + BusinessObjectType.LIDI_BUSINESS_OBJECTS.size();

    // Then
    assertThat(assigned).containsExactlyInAnyOrderElementsOf(Arrays.asList(BusinessObjectType.values()));
    assertThat(totalAssignments).isEqualTo(BusinessObjectType.values().length);
  }
}
