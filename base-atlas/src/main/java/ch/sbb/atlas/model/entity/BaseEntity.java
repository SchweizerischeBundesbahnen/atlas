package ch.sbb.atlas.model.entity;

import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity {

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  @AtlasVersionableProperty(ignoreDiff = true)
  private LocalDateTime creationDate;

  @Column(updatable = false)
  @AtlasVersionableProperty(ignoreDiff = true)
  private String creator;

  @UpdateTimestamp
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
    String sbbUid = UserService.getUserIdentifier();
    setCreator(sbbUid);
    setEditor(sbbUid);
  }

  @PreUpdate
  public void onPreUpdate() {
    setEditor(UserService.getUserIdentifier());
  }

}
