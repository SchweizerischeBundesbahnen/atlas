import {
  enableProdMode,
  ErrorHandler,
  importProvidersFrom,
} from '@angular/core';

import { environment } from './environments/environment';
import { GlobalErrorHandler } from './app/core/configuration/global-error-handler';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { ServerErrorInterceptor } from './app/core/configuration/server-error-interceptor';
import { CoreModule } from './app/core/module/core.module';
import { DateModule } from './app/core/module/date.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app/app-routing.module';
import { AtlasApiModule, Configuration } from './app/api';
import { ServiceWorkerModule } from '@angular/service-worker';
import { AppComponent } from './app/app.component';
import { bootstrapApplication } from '@angular/platform-browser';

const httpLoaderFactory = (http: HttpClient) => new TranslateHttpLoader(http);

function withBasePath(basePath: string) {
  return () => new Configuration({ basePath: basePath });
}

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
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
}).catch((err) => console.error(err));
