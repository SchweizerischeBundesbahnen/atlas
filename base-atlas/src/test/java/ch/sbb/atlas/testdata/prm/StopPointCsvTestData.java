package ch.sbb.atlas.testdata.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointCsvTestData {

  public List<StopPointCsvModel> getStopPointCsvModels() {
    List<StopPointCsvModel> stopPointCsvModels = new ArrayList<>();
    StopPointCsvModel stopPointCsvModel = getStopPointCsvModel();
    stopPointCsvModels.add(stopPointCsvModel);
    return stopPointCsvModels;
  }

  public StopPointCsvModel getStopPointCsvModel() {
    return StopPointCsvModel.builder()
        .address("address")
        .didokCode(8534567)
        .alternativeTransport(0)
        .transportationMeans("~Z~")
        .wheelchairTickMach(0)
        .visualInfos(0)
        .alternativeTransportCondition("no-alternative")
        .assistanceCondition("no assistance")
        .assistanceAvailability(0)
        .audioTickMach(0)
        .city("Bern")
        .compInfos("comp info")
        .sloid("ch:1:sloid:12345")
        .assistanceReqsFulfilled(0)
        .dynamicAudioSys(0)
        .dynamicOpticSys(0)
        .assistanceReqsFulfilled(0)
        .freeText("Free")
        .url("www.ich.du")
        .assistanceService(0)
        .interoperable(0)
        .ticketMachine(0)
        .status(1)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();
  }

  public List<ItemImportResult> getStopPointItemImportResults(
      List<StopPointCsvModelContainer> stopPointCsvModelContainers) {
    List<ItemImportResult> itemImportResults = new ArrayList<>();
    for (StopPointCsvModelContainer container : stopPointCsvModelContainers) {
      ItemImportResult itemImportResult = new ItemImportResult();
      itemImportResult.setItemNumber(container.getDidokCode().toString());
      itemImportResult.setStatus(ItemImportResponseStatus.SUCCESS);
      itemImportResults.add(itemImportResult);
    }
    return itemImportResults;
  }

  public StopPointCsvModelContainer getStopPointCsvModelContainer() {
    List<StopPointCsvModel> csvModelsToUpdate = getStopPointCsvModels();
    StopPointCsvModelContainer servicePointCsvModelContainer = new StopPointCsvModelContainer();
    servicePointCsvModelContainer.setDidokCode(csvModelsToUpdate.get(0).getDidokCode());
    servicePointCsvModelContainer.setStopPointCsvModels(csvModelsToUpdate);
    return servicePointCsvModelContainer;
  }


}
