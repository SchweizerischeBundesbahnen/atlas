package ch.sbb.line.directory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.entity.BusinessIdGeneration;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.base.service.versioning.model.VersionableProperty.RelationType;
import ch.sbb.atlas.user.administration.security.BusinessOrganisationAssociated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
@ToString
@SuperBuilder
@Entity(name = "timetable_field_number_version")
@FieldNameConstants
@AtlasVersionable
public class TimetableFieldNumberVersion extends BaseVersion implements Versionable,
    BusinessOrganisationAssociated {

  private static final String VERSION_SEQ = "timetable_field_number_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Builder.Default
  @OneToMany(mappedBy = "timetableFieldNumberVersion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @AtlasVersionableProperty(relationType = RelationType.ONE_TO_MANY,
      relationsFields = {TimetableFieldLineRelation.Fields.slnid}
  )
  private Set<TimetableFieldLineRelation> lineRelations = new HashSet<>();

  @AtlasVersionableProperty
  @BusinessIdGeneration(valueGenerator = TtfnidGenerator.class)
  @Column(updatable = false)
  private String ttfnid;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String description;

  @AtlasVersionableProperty
  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String number;

  @AtlasVersionableProperty
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String swissTimetableFieldNumber;

  @Column(columnDefinition = "TIMESTAMP")
  @NotNull
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  @NotNull
  private LocalDate validTo;

  @AtlasVersionableProperty
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String businessOrganisation;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_1500)
  private String comment;

}
