import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { BusinessOrganisationsService } from './api/businessOrganisations.service';
import { CompaniesService } from './api/companies.service';
import { LinesService } from './api/lines.service';
import { SublinesService } from './api/sublines.service';
import { TimetableFieldNumbersService } from './api/timetableFieldNumbers.service';
import { TransportCompaniesService } from './api/transportCompanies.service';
import { TransportCompanyRelationsService } from './api/transportCompanyRelations.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: [],
})
export class AtlasApiModule {
  public static forRoot(
    configurationFactory: () => Configuration
  ): ModuleWithProviders<AtlasApiModule> {
    return {
      ngModule: AtlasApiModule,
      providers: [{ provide: Configuration, useFactory: configurationFactory }],
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: AtlasApiModule, @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('AtlasApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error(
        'You need to import the HttpClientModule in your AppModule! \n' +
          'See also https://github.com/angular/angular/issues/20575'
      );
    }
  }
}
