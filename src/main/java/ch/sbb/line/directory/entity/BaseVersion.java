package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.api.SequenctialValidRange;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import ch.sbb.line.directory.service.UserService;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseVersion implements SequenctialValidRange {

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
