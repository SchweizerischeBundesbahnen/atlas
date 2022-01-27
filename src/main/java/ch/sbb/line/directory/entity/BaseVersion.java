package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.validation.DatesValidator;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.line.directory.service.UserService;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
public abstract class BaseVersion implements DatesValidator {

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
  @AtlasVersionableProperty(ignoreDiff = true)
  private Integer version;

  @PrePersist
  public void onPrePersist() {
    String sbbUid = UserService.getSbbUid();
    setCreator(sbbUid);
    setEditor(sbbUid);
  }

  @PreUpdate
  public void onPreUpdate() {
    setEditor(UserService.getSbbUid());
  }
}
