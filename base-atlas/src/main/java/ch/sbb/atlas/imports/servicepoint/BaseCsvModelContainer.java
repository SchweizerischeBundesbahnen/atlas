package ch.sbb.atlas.imports.servicepoint;

import ch.sbb.atlas.versioning.date.DateHelper;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
          previous.setValidTo(current.getValidTo());
        } else {
          csvModelListMerged.add(current);
        }
      }
      csvModelList = csvModelListMerged;
    }
  }

}
