package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.api.SequenctialValidRange;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import ch.sbb.line.directory.swiss.number.SwissNumber;
import ch.sbb.line.directory.swiss.number.SwissNumberDescriptor;
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
@Entity(name = "line_version")
public class LineVersion implements SequenctialValidRange, SwissNumber {

  private static final String VERSION_SEQ = "line_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
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
  private LineType type;

  @NotNull
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  @Size(max = 50)
  private String shortName;

  @Size(max = 50)
  private String alternativeName;

  @Size(max = 500)
  private String combinationName;

  @Size(max = 1000)
  private String longName;

  @Convert(converter = RgbColorConverter.class)
  private RgbColor colorFontRgb;

  @Convert(converter = RgbColorConverter.class)
  private RgbColor colorBackRgb;

  @Convert(converter = CmykColorConverter.class)
  private CmykColor colorFontCmyk;

  @Convert(converter = CmykColorConverter.class)
  private CmykColor colorBackCmyk;

  @Size(max = 255)
  private String icon;

  @Size(max = 255)
  private String description;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @NotBlank
  @Size(max = 50)
  private String businessOrganisation;

  @Size(max = 1500)
  private String comment;

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
    return new SwissNumberDescriptor(Fields.swissLineNumber, this::getSwissLineNumber);
  }
}
