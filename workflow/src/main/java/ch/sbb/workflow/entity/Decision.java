package ch.sbb.workflow.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.service.UserService;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "decision")
public class Decision {

  private static final String VERSION_SEQ = "decision_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  //TODO: replace wit enum YES/NO
  private Boolean judgement;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String motivation;

  @OneToOne
  @JoinColumn(name = "examinant_id", referencedColumnName = "id")
  private Person examinant;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime motivationDate;

  //TODO: replace wit enum YES/NO
  private Boolean fotJudgement;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String fotMotivation;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "fot_overrider_id", referencedColumnName = "id")
  private Person fotOverrider;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime fotMotivationDate;

  @Column(updatable = false)
  private String creator;

  private String editor;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

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
