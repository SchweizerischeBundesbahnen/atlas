package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion_;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointServicePointSpecification;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@Builder
public class LoadingPointSearchRestrictions {

    private final Pageable pageable;
    private final LoadingPointRequestParams loadingPointRequestParams;

    @Singular(ignoreNullCollections = true)
    private List<String> searchCriterias;

    public Specification<LoadingPointVersion> getSpecification() {
        return specBuilder().searchCriteriaSpecification(searchCriterias)
                .and(specBuilder().validOnSpecification(Optional.ofNullable(loadingPointRequestParams.getValidOn())))
                .and(specBuilder().inSpecification(loadingPointRequestParams.getNumbers(),
                    LoadingPointVersion.Fields.number))
                .and(specBuilder().inSpecification(loadingPointRequestParams.getServicePointNumbersWithoutDigits(),
                    LoadingPointVersion.Fields.servicePointNumber))
                .and(new LoadingPointServicePointSpecification<>(
                        loadingPointRequestParams.getSboids(),
                        loadingPointRequestParams.getServicePointNumbersShorts(),
                        loadingPointRequestParams.getServicePointUicCountryCodes().stream()
                                .map(Country::from).toList(),
                        loadingPointRequestParams.getServicePointSloids()
                ))
                .and(new ValidOrEditionTimerangeSpecification<>(
                        loadingPointRequestParams.getFromDate(),
                        loadingPointRequestParams.getToDate(),
                        loadingPointRequestParams.getCreatedAfter(),
                        loadingPointRequestParams.getModifiedAfter()));

    }

    protected SpecificationBuilder<LoadingPointVersion> specBuilder() {
        return SpecificationBuilder.<LoadingPointVersion>builder()
                .stringAttributes(List.of(LoadingPointVersion.Fields.number))
                .validFromAttribute(LoadingPointVersion_.validFrom)
                .validToAttribute(LoadingPointVersion_.validTo)
                .build();
    }
}
