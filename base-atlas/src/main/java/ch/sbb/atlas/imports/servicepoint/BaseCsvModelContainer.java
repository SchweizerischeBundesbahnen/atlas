package ch.sbb.atlas.imports.servicepoint;

import ch.sbb.atlas.versioning.date.DateHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
@SuperBuilder
@Getter
public abstract class BaseCsvModelContainer<T extends BaseDidokCsvModel> {

  public List<T> csvModelList;

  public void mergeWhenDatesAreSequentialAndModelsAreEqual() {
    if (csvModelList.size() > 1) {
      final List<T> csvModelListMerged = new ArrayList<>(List.of(csvModelList.get(0)));
      for (int csvModelIndex = 1; csvModelIndex < csvModelList.size(); csvModelIndex++) {
        final T previous = csvModelListMerged.get(csvModelListMerged.size() - 1);
        final T current = csvModelList.get(csvModelIndex);
        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom()) && current.equals(previous)) {
          mergePreviousAndCurrentVersion(previous, current);
        } else {
          csvModelListMerged.add(current);
        }
      }
      csvModelList = csvModelListMerged;
    }
  }

  private void mergePreviousAndCurrentVersion(T previous, T current) {
    logFoundVersionsToMerge();
    log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
    log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
    previous.setValidTo(current.getValidTo());
    log.info("Version merged [{}]-[{}]", previous.getValidFrom(), current.getValidTo());
  }

  protected abstract void logFoundVersionsToMerge();



}
