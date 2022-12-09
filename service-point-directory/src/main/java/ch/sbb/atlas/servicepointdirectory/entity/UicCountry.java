package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "uic_country")
public class UicCountry extends BaseEntity {

  private static final String VERSION_SEQ = "uic_country_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Column(name = "iso_3166_1_alpha_2")
  private String isoCode;

  @NotNull
  @AtlasVersionableProperty
  @Column(name = "uic_920_14")
  private Integer uicCode;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String nameEn;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String nameFr;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String nameDe;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String nameIt;

}
