import {
  ApplicationConfig,
  enableProdMode,
  ErrorHandler,
  importProvidersFrom,
} from '@angular/core';
import { CoreModule } from './core/module/core.module';
import { DateModule } from './core/module/date.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AtlasApiModule, Configuration } from './api';
import { environment } from '../environments/environment';
import { ServiceWorkerModule } from '@angular/service-worker';
import { GlobalErrorHandler } from './core/configuration/global-error-handler';
import { ServerErrorInterceptor } from './core/configuration/server-error-interceptor';
import { provideAnimations } from '@angular/platform-browser/animations';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

const httpLoaderFactory = (http: HttpClient) => new TranslateHttpLoader(http);

function withBasePath(basePath: string) {
  return () => new Configuration({ basePath: basePath });
}

if (environment.production) {
  enableProdMode();
}

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(
      CoreModule,
      DateModule.forRoot(),
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: httpLoaderFactory,
          deps: [HttpClient],
        },
      }),
      AppRoutingModule,
      AtlasApiModule.forRoot(withBasePath(environment.atlasUnauthApiUrl)),
      ServiceWorkerModule.register('ngsw-worker.js', {
        enabled: environment.production,
        // Register the ServiceWorker as soon as the application is stable
        // or after 30 seconds (whichever comes first).
        registrationStrategy: 'registerWhenStable:30000',
      })
    ),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ServerErrorInterceptor,
      multi: true,
    },
    provideAnimations(),
  ],
};
