package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.api.SequenctialValidRange;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.swiss.number.SwissNumber;
import ch.sbb.line.directory.swiss.number.SwissNumberDescriptor;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Builder
@FieldNameConstants
@Entity(name = "subline_version")
public class SublineVersion implements SequenctialValidRange, SwissNumber {

  private static final String SUBLINE_VERSION_SEQ = "subline_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SUBLINE_VERSION_SEQ)
  @SequenceGenerator(name = SUBLINE_VERSION_SEQ, sequenceName = SUBLINE_VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = 50)
  private String swissSublineNumber;

  @Size(max = 50)
  private String swissLineNumber;

  @GeneratorType(type = SlnidGenerator.class, when = GenerationTime.INSERT)
  @Column(updatable = false, unique = true)
  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SublineType type;

  @Size(max = 255)
  private String description;

  @Size(max = 50)
  private String shortName;

  @Size(max = 1000)
  private String longName;

  @NotNull
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @NotBlank
  @Size(max = 50)
  private String businessOrganisation;

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

  @Override
  public SwissNumberDescriptor getSwissNumberDescriptor() {
    return new SwissNumberDescriptor(Fields.swissSublineNumber, this::getSwissSublineNumber);
  }
}
