package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "service_point_number")
public class ServicePointNumber extends BaseEntity {

  private static final String VERSION_SEQ = "service_point_number_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @NotNull
  private Integer number;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Country country;

  private boolean used;
}
