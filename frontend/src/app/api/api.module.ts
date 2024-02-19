import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { BusinessOrganisationsService } from './api/businessOrganisations.service';
import { ClientCredentialAdministrationService } from './api/clientCredentialAdministration.service';
import { CompaniesService } from './api/companies.service';
import { ExportServicePointBatchService } from './api/exportServicePointBatch.service';
import { GeoDataService } from './api/geoData.service';
import { ImportPrmBatchService } from './api/importPrmBatch.service';
import { ImportServicePointBatchService } from './api/importServicePointBatch.service';
import { LinesService } from './api/lines.service';
import { LoadingPointsService } from './api/loadingPoints.service';
import { PersonWithReducedMobilityService } from './api/personWithReducedMobility.service';
import { PersonWithReducedMobilityExportService } from './api/personWithReducedMobilityExport.service';
import { ServicePointsService } from './api/servicePoints.service';
import { SublinesService } from './api/sublines.service';
import { TimetableFieldNumbersService } from './api/timetableFieldNumbers.service';
import { TimetableHearingStatementsService } from './api/timetableHearingStatements.service';
import { TimetableHearingYearsService } from './api/timetableHearingYears.service';
import { TimetableYearChangeService } from './api/timetableYearChange.service';
import { TrafficPointElementsService } from './api/trafficPointElements.service';
import { TransportCompaniesService } from './api/transportCompanies.service';
import { TransportCompanyRelationsService } from './api/transportCompanyRelations.service';
import { UserAdministrationService } from './api/userAdministration.service';
import { UserInformationService } from './api/userInformation.service';
import { WorkflowService } from './api/workflow.service';

@NgModule({
  imports:      [],
  declarations: [],
  exports:      [],
  providers: []
})
export class AtlasApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders<AtlasApiModule> {
        return {
            ngModule: AtlasApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        };
    }

    constructor( @Optional() @SkipSelf() parentModule: AtlasApiModule,
                 @Optional() http: HttpClient) {
        if (parentModule) {
            throw new Error('AtlasApiModule is already loaded. Import in your base AppModule only.');
        }
        if (!http) {
            throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
            'See also https://github.com/angular/angular/issues/20575');
        }
    }
}
