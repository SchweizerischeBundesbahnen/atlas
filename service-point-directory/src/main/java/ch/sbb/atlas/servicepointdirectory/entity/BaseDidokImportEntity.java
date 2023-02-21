package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.service.UserService;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
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

  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  @AtlasVersionableProperty(ignoreDiff = true)
  private LocalDateTime creationDate;

  @Column(updatable = false)
  @AtlasVersionableProperty(ignoreDiff = true)
  private String creator;

  @Column(columnDefinition = "TIMESTAMP")
  @AtlasVersionableProperty(ignoreDiff = true)
  private LocalDateTime editionDate;

  @AtlasVersionableProperty(ignoreDiff = true)
  private String editor;

  @Version
  @NotNull
  @AtlasVersionableProperty(ignoreDiff = true, doNotOverride = true)
  private Integer version;

  @PrePersist
  public void onPrePersist() {
    String sbbUid = UserService.getSbbUid();
    setCreator(Optional.ofNullable(creator).orElse(sbbUid));
    setEditor(Optional.ofNullable(editor).orElse(sbbUid));

    setCreationDate(Optional.ofNullable(creationDate).orElse(LocalDateTime.now()));
    setEditionDate(Optional.ofNullable(editionDate).orElse(LocalDateTime.now()));
  }

  @PreUpdate
  public void onPreUpdate() {
    String sbbUid = UserService.getSbbUid();
    setEditor(Optional.ofNullable(editor).orElse(sbbUid));
    setEditionDate(Optional.ofNullable(editionDate).orElse(LocalDateTime.now()));
  }

}
