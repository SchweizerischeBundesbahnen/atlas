package ch.sbb.prm.directory.search;

import ch.sbb.prm.directory.controller.model.ContactPointObjectRequestParams;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import java.util.List;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class ContactPointSearchRestrictions extends BasePrmSearchRestrictions<ContactPointVersion> {

  private final Pageable pageable;
  private final ContactPointObjectRequestParams contactPointObjectRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  @Override
  public Specification<ContactPointVersion> getSpecification() {
    super.setPageable(pageable);
    super.setPrmObjectRequestParams(contactPointObjectRequestParams);
    return Specification.allOf(super.getSpecification())
        .and(specBuilder().inSpecification(contactPointObjectRequestParams.getContactPointTypes(),
            ContactPointVersion.Fields.type));
  }

}
