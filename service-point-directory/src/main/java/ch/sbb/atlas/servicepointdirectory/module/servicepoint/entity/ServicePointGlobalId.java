package ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.converter.ServicePointNumberConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * 1:1 mapping between a service point (identified by its stable {@link ServicePointNumber}) and an
 * official international Global-ID (e.g. Germany {@code de:05770:1282}, Austria {@code at:42:9379}).
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@FieldNameConstants
@Entity(name = "service_point_global_id")
@Table(name = "service_point_global_id", uniqueConstraints = {
    @UniqueConstraint(name = "uq_service_point_global_id_service_point_number", columnNames = "service_point_number"),
    @UniqueConstraint(name = "uq_service_point_global_id_global_id", columnNames = "global_id")})
public class ServicePointGlobalId {

  private static final String SEQ = "service_point_global_id_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ)
  @SequenceGenerator(name = SEQ, sequenceName = SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @Convert(converter = ServicePointNumberConverter.class)
  @Column(name = "service_point_number", nullable = false)
  private ServicePointNumber servicePointNumber;

  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Column(name = "global_id", nullable = false)
  private String globalId;

}
