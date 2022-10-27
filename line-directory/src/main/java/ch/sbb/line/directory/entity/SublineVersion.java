package ch.sbb.line.directory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.user.administration.security.BusinessOrganisationAssociated;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.SublineType;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "subline_version")
@AtlasVersionable
public class SublineVersion extends BaseVersion implements Versionable,
    BusinessOrganisationAssociated {

  private static final String SUBLINE_VERSION_SEQ = "subline_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SUBLINE_VERSION_SEQ)
  @SequenceGenerator(name = SUBLINE_VERSION_SEQ, sequenceName = SUBLINE_VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String swissSublineNumber;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  @NotBlank
  @AtlasVersionableProperty
  private String mainlineSlnid;

  @GeneratorType(type = SlnidGenerator.class, when = GenerationTime.INSERT)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SublineType sublineType;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String description;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String number;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String longName;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private PaymentType paymentType;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String businessOrganisation;

}
