package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.converter.CymkColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CymkColor;
import ch.sbb.line.directory.model.RgbColor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "line_version")
public class LineVersion {

  private static final String VERSION_SEQ = "line_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  private String swissLineNumber;

  private String slnid;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private LineType type;

  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  private String shortName;

  private String alternativeName;

  private String combinationName;

  private String longName;

  @Convert(converter = RgbColorConverter.class)
  private RgbColor colorFontRgb;

  @Convert(converter = RgbColorConverter.class)
  private RgbColor colorBackRgb;

  @Convert(converter = CymkColorConverter.class)
  private CymkColor colorFontCmyk;

  @Convert(converter = CymkColorConverter.class)
  private CymkColor colorBackCmyk;

  private String icon;

  private String description;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  private String businessOrganisation;

  private String comment;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime creationDate;

  private String creator;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  private String editor;

}
