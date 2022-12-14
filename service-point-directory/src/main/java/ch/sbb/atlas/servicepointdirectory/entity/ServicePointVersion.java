package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.servicepointdirectory.converter.CategoryConverter;
import ch.sbb.atlas.servicepointdirectory.converter.MeanOfTransportConverter;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPlaceType;
import ch.sbb.atlas.user.administration.security.BusinessOrganisationAssociated;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "service_point_version")
@AtlasVersionable
public class ServicePointVersion extends BaseVersion implements Versionable,
        BusinessOrganisationAssociated {

    private static final String VERSION_SEQ = "service_point_version_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
    @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
    private Long id;

    @NotNull
    @AtlasVersionableProperty
    private Integer number;

    @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
    @AtlasVersionableProperty
    private String sloid;

    @NotNull
    @AtlasVersionableProperty
    private Integer checkDigit;

    @NotNull
    @AtlasVersionableProperty
    private Integer numberShort;

    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "uic_country_code", referencedColumnName = "uic_920_14")
    private UicCountry uicCountry;

    @NotBlank
    @Size(max = AtlasFieldLengths.LENGTH_50)
    @AtlasVersionableProperty
    private String designationLong;

    @NotBlank
    @Size(max = AtlasFieldLengths.LENGTH_30)
    @AtlasVersionableProperty
    private String designationOfficial;

    @NotBlank
    @Size(max = AtlasFieldLengths.LENGTH_6)
    @AtlasVersionableProperty
    private String abbreviation;

    @Size(max = AtlasFieldLengths.LENGTH_10)
    @AtlasVersionableProperty
    private String sortCodeOfDestinationStation;

    @NotNull
    @AtlasVersionableProperty
    private Integer statusDidok3;

    @AtlasVersionableProperty
    private boolean hasGeolocation;

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate validFrom;

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate validTo;

    @NotBlank
    @Size(max = AtlasFieldLengths.LENGTH_50)
    @AtlasVersionableProperty
    private String businessOrganisation;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "servicePointVersion")
    private ServicePointGeolocation servicePointGeolocation;

    @AtlasVersionableProperty
    @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)
    @Convert(converter = CategoryConverter.class)
    private Set<Category> categories;

    @Enumerated(EnumType.STRING)
    private OperatingPointType operatingPointType;

    public boolean isOperatingPoint() {
        return operatingPointType != null;
    }

    public Set<Category> getCategories() {
        if (categories == null) {
            return new HashSet<>();
        }
        return categories;
    }

    public boolean isStopPlace() {
        return !getMeanOfTransport().isEmpty();
    }

    @AtlasVersionableProperty
    @ElementCollection(targetClass = MeanOfTransport.class, fetch = FetchType.EAGER)
    @Convert(converter = MeanOfTransportConverter.class)
    private Set<MeanOfTransport> meansOfTransport;

    public Set<MeanOfTransport> getMeanOfTransport() {
        if (meansOfTransport == null) {
            return new HashSet<>();
        }
        return meansOfTransport;
    }

    @Enumerated(EnumType.STRING)
    private StopPlaceType stopPlaceType;

    @AssertTrue(message = "StopPlaceType only allowed for StopPlaces")
    boolean isValidStopPlace() {
        return isStopPlace() || stopPlaceType == null;
    }
}
