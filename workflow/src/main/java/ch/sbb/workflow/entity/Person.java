package ch.sbb.workflow.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String mail;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

}
