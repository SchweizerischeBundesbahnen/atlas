package ch.sbb.atlas.imports;

import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.versioning.model.Versionable;
import java.util.List;
import java.util.stream.Collectors;
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

  protected ItemImportResult buildWarningImportResult(T element,  List<Exception> exceptions){
    String combinedWarningMessage = exceptions.stream()
        .map(Exception::getMessage)
        .collect(Collectors.joining(", ", "[WARNING]: This version was imported successfully but it has warnings: ", ""));

    ItemImportResultBuilder warningResultBuilder = ItemImportResult.warningResultBuilder(combinedWarningMessage);

    return addInfoToItemImportResult(warningResultBuilder, element);
  }


}
