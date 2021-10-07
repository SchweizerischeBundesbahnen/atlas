import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { LinesService } from './api/lines.service';
import { SublinesService } from './api/sublines.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: [],
})
export class LiDiApiModule {
  public static forRoot(
    configurationFactory: () => Configuration
  ): ModuleWithProviders<LiDiApiModule> {
    return {
      ngModule: LiDiApiModule,
      providers: [{ provide: Configuration, useFactory: configurationFactory }],
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: LiDiApiModule, @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('LiDiApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error(
        'You need to import the HttpClientModule in your AppModule! \n' +
          'See also https://github.com/angular/angular/issues/20575'
      );
    }
  }
}
