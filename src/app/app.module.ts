import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/module/core.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeComponent } from './pages/home/home.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { environment } from '../environments/environment';
import { AtlasApiModule, Configuration } from './api';

// AoT requires an exported function for factories
const httpLoaderFactory = (http: HttpClient) => new TranslateHttpLoader(http);

function withBasePath(basePath: string) {
  return () => new Configuration({ basePath: basePath });
}

@NgModule({
  declarations: [AppComponent, HomeComponent],
  imports: [
    CoreModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    BrowserAnimationsModule,
    AppRoutingModule,
    AtlasApiModule.forRoot(withBasePath(environment.atlasApiUrl)),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
