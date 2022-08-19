export * from './atlasGraphApi.service';
import { AtlasGraphApiService } from './atlasGraphApi.service';
export * from './businessOrganisations.service';
import { BusinessOrganisationsService } from './businessOrganisations.service';
export * from './companies.service';
import { CompaniesService } from './companies.service';
export * from './futureTimetable.service';
import { FutureTimetableService } from './futureTimetable.service';
export * from './liDiUserAdministration.service';
import { LiDiUserAdministrationService } from './liDiUserAdministration.service';
export * from './lines.service';
import { LinesService } from './lines.service';
export * from './sublines.service';
import { SublinesService } from './sublines.service';
export * from './timetableFieldNumbers.service';
import { TimetableFieldNumbersService } from './timetableFieldNumbers.service';
export * from './transportCompanies.service';
import { TransportCompaniesService } from './transportCompanies.service';
export * from './transportCompanyRelations.service';
import { TransportCompanyRelationsService } from './transportCompanyRelations.service';
export const APIS = [
  AtlasGraphApiService,
  BusinessOrganisationsService,
  CompaniesService,
  FutureTimetableService,
  LiDiUserAdministrationService,
  LinesService,
  SublinesService,
  TimetableFieldNumbersService,
  TransportCompaniesService,
  TransportCompanyRelationsService,
];
