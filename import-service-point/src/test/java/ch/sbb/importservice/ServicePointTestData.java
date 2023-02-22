package ch.sbb.importservice;

import ch.sbb.atlas.imports.servicepoint.model.ItemImportResponseStatus;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointTestData {

  public List<ServicePointItemImportResult> getServicePointItemImportResults(
      List<ServicePointCsvModelContainer> servicePointCsvModelContainers) {
    ServicePointImportReqModel servicePointImportReqModel = new ServicePointImportReqModel();
    servicePointImportReqModel.setServicePointCsvModelContainers(servicePointCsvModelContainers);

    List<ServicePointItemImportResult> servicePointItemImportResults = new ArrayList<>();
    for (ServicePointCsvModelContainer container : servicePointCsvModelContainers) {

      ServicePointItemImportResult servicePointItemImportResult = new ServicePointItemImportResult();
      servicePointItemImportResult.setItemNumber(container.getDidokCode());
      servicePointItemImportResult.setStatus(ItemImportResponseStatus.SUCCESS);
      servicePointItemImportResults.add(servicePointItemImportResult);
    }
    return servicePointItemImportResults;
  }

  public List<ServicePointCsvModelContainer> getServicePointCsvModelContainers() {
    ServicePointCsvModelContainer servicePointCsvModelContainer1 = getServicePointCsvModelContainer(123);
    ServicePointCsvModelContainer servicePointCsvModelContainer2 = getServicePointCsvModelContainer(124);

    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointCsvModelContainers.add(servicePointCsvModelContainer1);
    servicePointCsvModelContainers.add(servicePointCsvModelContainer2);
    return servicePointCsvModelContainers;
  }

  private ServicePointCsvModelContainer getServicePointCsvModelContainer(Integer didokNumber) {
    List<ServicePointCsvModel> csvModelsToUpdate = getDefaultServicePointCsvModels(
        didokNumber);
    ServicePointCsvModelContainer servicePointCsvModelContainer = new ServicePointCsvModelContainer();
    servicePointCsvModelContainer.setDidokCode(didokNumber);
    servicePointCsvModelContainer.setServicePointCsvModelList(csvModelsToUpdate);
    return servicePointCsvModelContainer;
  }

  public List<ServicePointCsvModel> getDefaultServicePointCsvModels(Integer didokNumber) {
    ServicePointCsvModel servicePointCsvModel1 = getServicePointModel(didokNumber, LocalDate.now(), LocalDate.now());
    ServicePointCsvModel servicePointCsvModel2 = getServicePointModel(didokNumber, LocalDate.now().plusMonths(1),
        LocalDate.now().plusMonths(1));
    List<ServicePointCsvModel> csvModelsToUpdate = new ArrayList<>();
    csvModelsToUpdate.add(servicePointCsvModel1);
    csvModelsToUpdate.add(servicePointCsvModel2);
    return csvModelsToUpdate;
  }

  private ServicePointCsvModel getServicePointModel(Integer didokNumber, LocalDate validFrom, LocalDate validTo) {
    ServicePointCsvModel servicePointCsvModel = new ServicePointCsvModel();
    servicePointCsvModel.setIsVirtuell(true);
    servicePointCsvModel.setValidFrom(validFrom);
    servicePointCsvModel.setValidTo(validTo);
    servicePointCsvModel.setNummer(didokNumber);
    servicePointCsvModel.setDidokCode(didokNumber);
    return servicePointCsvModel;
  }

}
