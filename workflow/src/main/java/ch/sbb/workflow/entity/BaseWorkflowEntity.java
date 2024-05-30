package ch.sbb.workflow.entity;

import ch.sbb.atlas.service.UserService;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
public abstract class BaseWorkflowEntity {

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @Column(updatable = false)
  private String creator;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  private String editor;

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
