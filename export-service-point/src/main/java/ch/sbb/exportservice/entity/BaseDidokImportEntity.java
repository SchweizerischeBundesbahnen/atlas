package ch.sbb.exportservice.entity;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
@FieldNameConstants
@Deprecated
/**
 * Switch back to ch.sbb.atlas.base.service.model.entity.BaseEntity once Didok dies
 */
public abstract class BaseDidokImportEntity {

  private LocalDateTime creationDate;

  private String creator;

  private LocalDateTime editionDate;

  private String editor;

  private Integer version;

}
