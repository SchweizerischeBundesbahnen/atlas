package ch.sbb.line.directory.entity;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import ch.sbb.line.directory.service.UserService;
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
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Entity(name = "line_version")
@AtlasVersionable
public class LineVersion extends BaseVersion implements Versionable {

  private static final String VERSION_SEQ = "line_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = 50)
  @AtlasVersionableProperty
  private String swissLineNumber;

  @GeneratorType(type = SlnidGenerator.class, when = GenerationTime.INSERT)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private LineType type;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private PaymentType paymentType;

  @Size(max = 50)
  @AtlasVersionableProperty
  private String number;

  @Size(max = 50)
  @AtlasVersionableProperty
  private String alternativeName;

  @Size(max = 50)
  @AtlasVersionableProperty
  private String combinationName;

  @Size(max = 255)
  @AtlasVersionableProperty
  private String longName;

  @Convert(converter = RgbColorConverter.class)
  @AtlasVersionableProperty
  private RgbColor colorFontRgb;

  @Convert(converter = RgbColorConverter.class)
  @AtlasVersionableProperty
  private RgbColor colorBackRgb;

  @Convert(converter = CmykColorConverter.class)
  @AtlasVersionableProperty
  private CmykColor colorFontCmyk;

  @Convert(converter = CmykColorConverter.class)
  @AtlasVersionableProperty
  private CmykColor colorBackCmyk;

  @Size(max = 255)
  @AtlasVersionableProperty
  private String icon;

  @Size(max = 255)
  @AtlasVersionableProperty
  private String description;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @NotBlank
  @Size(max = 50)
  @AtlasVersionableProperty
  private String businessOrganisation;

  @Size(max = 1500)
  @AtlasVersionableProperty
  private String comment;

}
