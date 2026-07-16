package ch.sbb.line.directory.module.line.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.entity.BusinessIdGeneration;
import ch.sbb.atlas.revoke.Revokable;
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
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "line_version")
@AtlasVersionable
public class LineVersion extends BaseVersion implements Versionable,
    BusinessOrganisationAssociated, Revokable {

  private static final String VERSION_SEQ = "line_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String swissLineNumber;

  @BusinessIdGeneration(valueGenerator = SlnidGenerator.class)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String slnid;

  @Size(max = AtlasFieldLengths.LENGTH_20)
  @AtlasVersionableProperty
  private String linienId;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private LineType lineType;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_8)
  @AtlasVersionableProperty
  private String number;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String longName;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String description;

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

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @AtlasVersionableProperty
  private String comment;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private LineConcessionType concessionType;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private OfferCategory offerCategory;

  @Size(max = AtlasFieldLengths.LENGTH_8)
  @AtlasVersionableProperty
  private String shortNumber;

}
