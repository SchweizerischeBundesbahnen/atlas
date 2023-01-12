package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.service.UserService;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
@Deprecated
/**
 * Switch back to ch.sbb.atlas.base.service.model.entity.BaseVersion once Didok dies
 */
public abstract class BaseDidokImportEntity implements DatesValidator {

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

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
  @AtlasVersionableProperty(ignoreDiff = true)
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
    setEditor(UserService.getSbbUid());
    setEditionDate(Optional.ofNullable(editionDate).orElse(LocalDateTime.now()));
  }

}
