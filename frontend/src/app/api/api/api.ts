export * from './businessOrganisations.service';
import { BusinessOrganisationsService } from './businessOrganisations.service';
export * from './companies.service';
import { CompaniesService } from './companies.service';
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
  BusinessOrganisationsService,
  CompaniesService,
  LinesService,
  SublinesService,
  TimetableFieldNumbersService,
  TransportCompaniesService,
  TransportCompanyRelationsService,
];
