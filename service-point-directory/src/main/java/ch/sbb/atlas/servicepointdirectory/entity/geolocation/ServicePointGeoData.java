package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDate;

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
      sp.number as number,
      sp.designation_official,
      case
          when sp.operating_point_technical_timetable_type is not null then 'OPERATING_POINT_TECHNICAL'
          when spvmot.means_of_transport is not null then
              (case
                when sp.freight_service_point then 'STOP_POINT_AND_FREIGHT_SERVICE_POINT'
                ELSE 'STOP_POINT'
              end)
          when sp.freight_service_point then 'FREIGHT_SERVICE_POINT'
          else 'SERVICE_POINT'
      end
      as service_point_type
      FROM service_point_version_geolocation geo
      JOIN service_point_version sp on sp.service_point_geolocation_id = geo.id 
      LEFT JOIN service_point_version_means_of_transport spvmot on sp.id = spvmot.service_point_version_id
      where sp.status!='REVOKED'
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
  private String designationOfficial;
  private Integer swissMunicipalityNumber;
  private String swissMunicipalityName;
  private String swissLocalityName;
  @Enumerated(EnumType.STRING)
  private ServicePointType servicePointType;
  @NotNull
  private LocalDate validFrom;
  @NotNull
  private LocalDate validTo;
}
