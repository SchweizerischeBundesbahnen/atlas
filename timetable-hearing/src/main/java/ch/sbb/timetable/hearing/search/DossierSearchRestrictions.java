package ch.sbb.timetable.hearing.search;

import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.LongSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.Dossier.Fields;
import ch.sbb.timetable.hearing.entity.Dossier_;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class DossierSearchRestrictions {

  private final Pageable pageable;

  private final DossierRequestParams requestParams;

  public Specification<Dossier> getSpecification() {
    return new EnumSpecification<>(requestParams.getCanton(), Dossier_.swissCanton)
        .and(new EnumSpecification<>(requestParams.getStatusRestrictions(), Dossier_.dossierStatus))
        .and(new LongSpecification<>(Dossier_.timetableYear, requestParams.getTimetableHearingYear()))
        .and(new SingleStringSpecification<>(requestParams.getBoContactSbbuid(), Fields.boContactSbbuid))
        .and(new SearchCriteriaSpecification<>(requestParams.getSearchCriterias(), List.of(Fields.id, Fields.topic)));
  }
}
