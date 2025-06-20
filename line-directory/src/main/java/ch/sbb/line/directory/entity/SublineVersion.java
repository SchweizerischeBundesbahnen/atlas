package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.entity.BusinessIdGeneration;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

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

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String swissSublineNumber;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  @NotBlank
  @AtlasVersionableProperty
  private String mainlineSlnid;

  @BusinessIdGeneration(valueGenerator = SublineSlnidGenerator.class)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String slnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SublineType sublineType;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String description;

  @Deprecated(forRemoval = true, since = "2.328.0")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String number;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String longName;

  @Deprecated(forRemoval = true, since = "2.328.0")
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

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SublineConcessionType concessionType;

}
