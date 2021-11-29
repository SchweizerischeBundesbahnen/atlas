package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.validation.DatesValidator;
import ch.sbb.line.directory.service.UserService;
import java.util.Date;
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
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseVersion implements DatesValidator {

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private Date creationDate;

  @Column(updatable = false)
  private String creator;

  @NotNull
  @Version
  @Column(columnDefinition = "TIMESTAMP")
  private Date editionDate;

  private String editor;

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
