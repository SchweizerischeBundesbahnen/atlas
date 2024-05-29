package ch.sbb.workflow.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
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
@Entity(name = "person")
public class Person {

  private static final String VERSION_SEQ = "person_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String firstName;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String lastName;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String function;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String mail;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stop_point_workflow_id")
  private StopPointWorkflow stopPointWorkflow;

}
