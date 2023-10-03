package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * Switch back to ch.sbb.atlas.model.entity.BaseEntity once Didok dies.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
@FieldNameConstants
@AtlasVersionable
@Deprecated
public abstract class BasePrmImportEntity {

  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  @AtlasVersionableProperty(ignoreDiff = true)
  private LocalDateTime creationDate;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Column(updatable = false)
  @AtlasVersionableProperty(ignoreDiff = true)
  private String creator;

  @Column(columnDefinition = "TIMESTAMP")
  @AtlasVersionableProperty(ignoreDiff = true, doNotOverride = true)
  private LocalDateTime editionDate;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty(ignoreDiff = true, doNotOverride = true)
  private String editor;

  @Version
  @NotNull
  @AtlasVersionableProperty(ignoreDiff = true, doNotOverride = true)
  private Integer version;

  @PrePersist
  public void onPrePersist() {
    String sbbUid = UserService.getUserIdentifier();
    setCreator(Optional.ofNullable(creator).orElse(sbbUid));
    setEditor(Optional.ofNullable(editor).orElse(sbbUid));

    setCreationDate(Optional.ofNullable(creationDate).orElse(LocalDateTime.now()));
    setEditionDate(Optional.ofNullable(editionDate).orElse(LocalDateTime.now()));
  }

  @PreUpdate
  public void onPreUpdate() {
    String sbbUid = UserService.getUserIdentifier();
    setEditor(Optional.ofNullable(editor).orElse(sbbUid));
    setEditionDate(Optional.ofNullable(editionDate).orElse(LocalDateTime.now()));
  }

}
