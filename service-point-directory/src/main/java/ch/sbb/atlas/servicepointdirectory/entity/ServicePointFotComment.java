package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.model.entity.BaseDidokImportEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
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
@Entity(name = "service_point_fot_comment")
public class ServicePointFotComment extends BaseDidokImportEntity {

  @Id
  private Integer servicePointNumber;

  @NotBlank
  private String fotComment;

}
