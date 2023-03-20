export * from './businessOrganisations.service';
import { BusinessOrganisationsService } from './businessOrganisations.service';
export * from './companies.service';
import { CompaniesService } from './companies.service';
export * from './importServicePointBatch.service';
import { ImportServicePointBatchService } from './importServicePointBatch.service';
export * from './lines.service';
import { LinesService } from './lines.service';
export * from './loadingPoints.service';
import { LoadingPointsService } from './loadingPoints.service';
export * from './servicePoints.service';
import { ServicePointsService } from './servicePoints.service';
export * from './sublines.service';
import { SublinesService } from './sublines.service';
export * from './timetableFieldNumbers.service';
import { TimetableFieldNumbersService } from './timetableFieldNumbers.service';
export * from './timetableHearing.service';
import { TimetableHearingService } from './timetableHearing.service';
export * from './timetableYearChange.service';
import { TimetableYearChangeService } from './timetableYearChange.service';
export * from './trafficPointElements.service';
import { TrafficPointElementsService } from './trafficPointElements.service';
export * from './transportCompanies.service';
import { TransportCompaniesService } from './transportCompanies.service';
export * from './transportCompanyRelations.service';
import { TransportCompanyRelationsService } from './transportCompanyRelations.service';
export * from './userAdministration.service';
import { UserAdministrationService } from './userAdministration.service';
export * from './userInformation.service';
import { UserInformationService } from './userInformation.service';
export * from './workflow.service';
import { WorkflowService } from './workflow.service';
export const APIS = [
  BusinessOrganisationsService,
  CompaniesService,
  ImportServicePointBatchService,
  LinesService,
  LoadingPointsService,
  ServicePointsService,
  SublinesService,
  TimetableFieldNumbersService,
  TimetableHearingService,
  TimetableYearChangeService,
  TrafficPointElementsService,
  TransportCompaniesService,
  TransportCompanyRelationsService,
  UserAdministrationService,
  UserInformationService,
  WorkflowService,
];
