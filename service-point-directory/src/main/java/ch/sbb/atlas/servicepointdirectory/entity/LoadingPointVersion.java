package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "loading_point_version")
@AtlasVersionable
public class LoadingPointVersion extends BaseEntity implements Versionable {

    private static final String VERSION_SEQ = "loading_point_version_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
    @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
    private Long id;

    @NotNull
    @AtlasVersionableProperty
    private Integer number;

    @NotNull
    @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
    @AtlasVersionableProperty
    private String designation;

    @Size(min = 1, max = AtlasFieldLengths.LENGTH_30)
    @AtlasVersionableProperty
    private String designationLong;

    @AtlasVersionableProperty
    private boolean connectionPoint;

    @NotNull
    @AtlasVersionableProperty
    private Long servicePointNumber;

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate validFrom;

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate validTo;

}
