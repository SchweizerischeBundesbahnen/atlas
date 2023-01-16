package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.servicepointdirectory.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Entity(name = "loading_point_version")
@AtlasVersionable
public class LoadingPointVersion extends BaseDidokImportEntity implements Versionable {

    private static final String VERSION_SEQ = "loading_point_version_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
    @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
    private Long id;

    @NotNull
    @AtlasVersionableProperty
    private Integer number;

    @NotNull
    @Size(min = 1, max = AtlasFieldLengths.LENGTH_12)
    @AtlasVersionableProperty
    private String designation;

    @Size(min = 1, max = AtlasFieldLengths.LENGTH_35)
    @AtlasVersionableProperty
    private String designationLong;

    @AtlasVersionableProperty
    private boolean connectionPoint;

    @NotNull
    @AtlasVersionableProperty
    @Convert(converter = ServicePointNumberConverter.class)
    @Valid
    private ServicePointNumber servicePointNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "loading_point_geolocation_id", referencedColumnName = "id")
    private LoadingPointGeolocation loadingPointGeolocation;

    public boolean hasGeolocation() {
        return loadingPointGeolocation != null;
    }

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate validFrom;

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate validTo;

}
