package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion_;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointElementRequestParams;
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
    private final LoadingPointElementRequestParams loadingPointElementRequestParams;

    @Singular(ignoreNullCollections = true)
    private List<String> searchCriterias;

    public Specification<LoadingPointVersion> getSpecification() {
        return specBuilder().searchCriteriaSpecification(searchCriterias)
                .and(specBuilder().validOnSpecification(Optional.ofNullable(loadingPointElementRequestParams.getValidOn())))
                .and(specBuilder().inSpecification(loadingPointElementRequestParams.getNumbers(),
                    LoadingPointVersion.Fields.number))
                .and(specBuilder().inSpecification(loadingPointElementRequestParams.getServicePointNumbersWithoutDigits(),
                    LoadingPointVersion.Fields.servicePointNumber))
                .and(new LoadingPointServicePointSpecification<>(
                        loadingPointElementRequestParams.getSboids(),
                        loadingPointElementRequestParams.getServicePointNumbersShorts(),
                        loadingPointElementRequestParams.getServicePointUicCountryCodes().stream()
                                .map(Country::from).toList(),
                        loadingPointElementRequestParams.getServicePointSloids()
                ))
                .and(new ValidOrEditionTimerangeSpecification<>(
                        loadingPointElementRequestParams.getFromDate(),
                        loadingPointElementRequestParams.getToDate(),
                        loadingPointElementRequestParams.getCreatedAfter(),
                        loadingPointElementRequestParams.getModifiedAfter()));

    }

    protected SpecificationBuilder<LoadingPointVersion> specBuilder() {
        return SpecificationBuilder.<LoadingPointVersion>builder()
                .stringAttributes(List.of(LoadingPointVersion.Fields.number))
                .validFromAttribute(LoadingPointVersion_.validFrom)
                .validToAttribute(LoadingPointVersion_.validTo)
                .build();
    }
}
