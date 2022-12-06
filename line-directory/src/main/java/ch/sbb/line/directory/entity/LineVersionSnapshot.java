package ch.sbb.line.directory.entity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.BaseVersionSnapshot;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.model.CmykColor;
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
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "line_version_snapshot")
public class LineVersionSnapshot extends BaseVersionSnapshot {

  private static final String VERSION_SNAPSHOT_SEQ = "line_version_snapshot_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SNAPSHOT_SEQ)
  @SequenceGenerator(name = VERSION_SNAPSHOT_SEQ, sequenceName = VERSION_SNAPSHOT_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkflowStatus workflowStatus;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String swissLineNumber;

  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private LineType lineType;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String number;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String alternativeName;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String combinationName;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String longName;

  @NotNull
  @Convert(converter = RgbColorConverter.class)
  private RgbColor colorFontRgb;

  @NotNull
  @Convert(converter = RgbColorConverter.class)
  private RgbColor colorBackRgb;

  @NotNull
  @Convert(converter = CmykColorConverter.class)
  private CmykColor colorFontCmyk;

  @NotNull
  @Convert(converter = CmykColorConverter.class)
  private CmykColor colorBackCmyk;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String icon;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String description;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String businessOrganisation;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String comment;

  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @Column(updatable = false)
  private String creator;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  private String editor;

  @Version
  @NotNull
  private Integer version;

}
