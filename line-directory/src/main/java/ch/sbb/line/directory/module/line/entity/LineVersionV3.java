package ch.sbb.line.directory.module.line.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.entity.BusinessIdGeneration;
import ch.sbb.atlas.versionable.AtlasVersionable;
import ch.sbb.atlas.versionable.AtlasVersionableProperty;
import ch.sbb.atlas.versionable.Versionable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = false)
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "line_version_v3")
@AtlasVersionable
public class LineVersionV3 extends BaseVersion implements BusinessOrganisationAssociated, Versionable {

  private static final String VERSION_SEQ = "line_version_v3_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  @BusinessIdGeneration(valueGenerator = SlnidGenerator.class)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String slnid;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String referencedLineSlnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private LineType lineType;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private ConcessionType concessionType;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String number;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String swissLineNumber;

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @AtlasVersionableProperty
  private String shortNumber;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private OfferCategory offerCategory;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String longName;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String description;

  @Size(max = AtlasFieldLengths.LENGTH_20)
  @AtlasVersionableProperty
  private String linienId;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @AtlasVersionableProperty
  private String comment;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String businessOrganisation;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private Financing financing;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String cablewayNumber;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String cablewayType;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String cablewayName;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = LineMeanOfTransport.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "line_version_v3_means_of_transport",
      joinColumns = @JoinColumn(name = "line_version_v3_id"))
  @Column(name = "means_of_transport")
  @Enumerated(EnumType.STRING)
  private Set<LineMeanOfTransport> meansOfTransport;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

}
