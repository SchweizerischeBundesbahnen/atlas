package ch.sbb.exportservice.job.sepodi.loadingpoint;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.sepodi.loadingpoint.LoadingPointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvLoadingPointVersionWriter extends BaseCsvWriter<LoadingPointVersionCsvModel> {

  CsvLoadingPointVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.number, Fields.designation, Fields.designationLong, Fields.connectionPoint, Fields.validFrom, Fields.validTo,
        Fields.servicePointNumber, Fields.checkDigit, Fields.parentSloidServicePoint, Fields.creationDate, Fields.editionDate,
        Fields.servicePointBusinessOrganisation, Fields.servicePointBusinessOrganisationNumber,
        Fields.servicePointBusinessOrganisationAbbreviationDe, Fields.servicePointBusinessOrganisationAbbreviationFr,
        Fields.servicePointBusinessOrganisationAbbreviationIt, Fields.servicePointBusinessOrganisationAbbreviationEn,
        Fields.servicePointBusinessOrganisationDescriptionDe, Fields.servicePointBusinessOrganisationDescriptionFr,
        Fields.servicePointBusinessOrganisationDescriptionIt, Fields.servicePointBusinessOrganisationDescriptionEn
    };
  }

}
