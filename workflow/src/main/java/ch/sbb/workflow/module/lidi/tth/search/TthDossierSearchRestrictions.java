package ch.sbb.workflow.module.lidi.tth.search;

import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.LongSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier.Fields;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier_;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class TthDossierSearchRestrictions {

  private final Pageable pageable;

  private final TthDossierRequestParams requestParams;

  public Specification<TthDossier> getSpecification() {
    return new EnumSpecification<>(requestParams.getCanton(), TthDossier_.swissCanton)
        .and(new EnumSpecification<>(requestParams.getStatusRestrictions(), TthDossier_.dossierStatus))
        .and(new LongSpecification<>(TthDossier_.timetableYear, requestParams.getTimetableHearingYear()))
        .and(new SingleStringSpecification<>(requestParams.getBoContactSbbuid(), Fields.boContactSbbuid))
        .and(new SearchCriteriaSpecification<>(requestParams.getSearchCriterias(), List.of(Fields.id, Fields.topic)));
  }
}
