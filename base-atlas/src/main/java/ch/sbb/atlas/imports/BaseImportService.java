package ch.sbb.atlas.imports;

import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.versioning.model.Versionable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseImportService<T extends  Versionable> {

  protected abstract void save(T element);

  protected abstract ItemImportResult addInfoToItemImportResult(
      ItemImportResultBuilder itemImportResultBuilder,
      T element
  );

  protected ItemImportResult buildSuccessImportResult(T element) {
    ItemImportResultBuilder successResultBuilder = ItemImportResult.successResultBuilder();
    return addInfoToItemImportResult(successResultBuilder, element);
  }

  protected ItemImportResult buildFailedImportResult(T element, Exception exception) {
    ItemImportResultBuilder failedResultBuilder = ItemImportResult.failedResultBuilder(exception);
    return addInfoToItemImportResult(failedResultBuilder, element);
  }

  protected ItemImportResult buildSuccessImportFailedHeightResult(T element, Exception exception){
    ItemImportResultBuilder failedResultBuilder = ItemImportResult.failedHeightResultBuilder(exception);
    return addInfoToItemImportResult(failedResultBuilder, element);
  }


}
