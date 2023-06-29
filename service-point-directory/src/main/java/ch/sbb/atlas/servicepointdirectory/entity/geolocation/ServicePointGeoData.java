package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Immutable
@Entity
@Subselect(ServicePointGeoData.VIEW_DEFINITION)
public class ServicePointGeoData extends GeolocationBaseEntity {

  static final String VIEW_DEFINITION = """
      SELECT geo.*,
      sp.valid_from,
      sp.valid_to,
      sp.sloid,
      sp.number
      FROM service_point_version_geolocation geo
      JOIN service_point_version sp on sp.service_point_geolocation_id = geo.id                                   
      """;
  @Id
  private Long id;
  private String sloid;
  private Integer number;
  @Enumerated(EnumType.STRING)
  private Country country;
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;
  private Integer swissDistrictNumber;
  private String swissDistrictName;
  private Integer swissMunicipalityNumber;
  private String swissMunicipalityName;
  private String swissLocalityName;
  @NotNull
  private LocalDate validFrom;
  @NotNull
  private LocalDate validTo;
}
