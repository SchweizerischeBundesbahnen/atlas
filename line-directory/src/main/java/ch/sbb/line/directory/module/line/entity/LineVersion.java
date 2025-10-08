package ch.sbb.line.directory.module.line.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.entity.BusinessIdGeneration;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.line.directory.module.workflow.entity.LineVersionWorkflow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    BusinessOrganisationAssociated {

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

  @Builder.Default
  @OneToMany(mappedBy = "lineVersion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<LineVersionWorkflow> lineVersionWorkflows = new HashSet<>();

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
