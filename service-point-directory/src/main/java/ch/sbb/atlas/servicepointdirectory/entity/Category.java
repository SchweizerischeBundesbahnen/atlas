package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "category")
@AtlasVersionable
public class Category extends BaseEntity {

  private static final String VERSION_SEQ = "category_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @AtlasVersionableProperty
  private boolean active;

  @AtlasVersionableProperty
  private boolean visible;

  @AtlasVersionableProperty
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_30)
  private String designationDe;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_30)
  private String designationFr;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_30)
  private String designationIt;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_30)
  private String designationEn;

  @AtlasVersionableProperty
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String description;

}
