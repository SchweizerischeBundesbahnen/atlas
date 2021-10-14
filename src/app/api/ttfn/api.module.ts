import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { TimetableFieldNumbersService } from './api/timetableFieldNumbers.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: [],
})
export class TtfnApiModule {
  public static forRoot(
    configurationFactory: () => Configuration
  ): ModuleWithProviders<TtfnApiModule> {
    return {
      ngModule: TtfnApiModule,
      providers: [{ provide: Configuration, useFactory: configurationFactory }],
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: TtfnApiModule, @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('TtfnApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error(
        'You need to import the HttpClientModule in your AppModule! \n' +
          'See also https://github.com/angular/angular/issues/20575'
      );
    }
  }
}
