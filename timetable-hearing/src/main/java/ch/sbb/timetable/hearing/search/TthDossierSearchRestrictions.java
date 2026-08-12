package ch.sbb.timetable.hearing.search;

import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.timetable.hearing.entity.TthDossier;
import ch.sbb.timetable.hearing.entity.TthDossier.Fields;
import ch.sbb.timetable.hearing.entity.TthDossier_;
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
        .and(new EnumSpecification<>(requestParams.getTimetableHearingYear(), TthDossier_.tthDossierYear))
        .and(new SingleStringSpecification<>(requestParams.getBoContactSbbuid(), Fields.boContactSbbuid))
        .and(new SearchCriteriaSpecification<>(requestParams.getSearchCriterias(), List.of(Fields.id, Fields.topic)));
  }
}
