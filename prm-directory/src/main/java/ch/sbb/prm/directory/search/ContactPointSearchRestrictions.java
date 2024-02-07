package ch.sbb.prm.directory.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.prm.directory.controller.model.ContactPointObjectRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.entity.ContactPointVersion_;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
@SuperBuilder
public class ContactPointSearchRestrictions {

    private final Pageable pageable;
    private final ContactPointObjectRequestParams contactPointObjectRequestParams;

    @Singular(ignoreNullCollections = true)
    private List<String> searchCriterias;

    public Specification<ContactPointVersion> getSpecification() {
        return specBuilder().searchCriteriaSpecification(searchCriterias)
                .and(specBuilder().validOnSpecification(Optional.ofNullable(contactPointObjectRequestParams.getValidOn())))
                .and(specBuilder().inSpecification(contactPointObjectRequestParams.getServicePointNumbers(), BasePrmEntityVersion.Fields.number))
                .and(specBuilder().inSpecification(contactPointObjectRequestParams.getSloids(), BasePrmEntityVersion.Fields.sloid))
                .and(specBuilder().inSpecification(contactPointObjectRequestParams.getContactPointTypes(), ContactPointVersion.Fields.type))
                .and(new ValidOrEditionTimerangeSpecification<>(
                        contactPointObjectRequestParams.getFromDate(),
                        contactPointObjectRequestParams.getToDate(),
                        contactPointObjectRequestParams.getCreatedAfter(),
                        contactPointObjectRequestParams.getModifiedAfter()));

    }

    protected SpecificationBuilder<ContactPointVersion> specBuilder() {
        return SpecificationBuilder.<ContactPointVersion>builder()
                .stringAttributes(List.of(BasePrmEntityVersion.Fields.number))
                .validFromAttribute(ContactPointVersion_.validFrom)
                .validToAttribute(ContactPointVersion_.validTo)
                .build();
    }
}
