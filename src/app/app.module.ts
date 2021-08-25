import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OAuthModule } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInsightsComponent } from './auth-insights/auth-insights.component';
import { HomeComponent } from './home/home.component';
import { environment } from '../environments/environment';
import { ApiModule, Configuration, ConfigurationParameters } from './api';
import { HeaderComponent } from './core/components/header/header.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { UserComponent } from './core/components/user/user.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatButtonModule } from '@angular/material/button';
import { MaterialModule } from './core/module/material.module';
import { LanguageSwitcherComponent } from './core/components/language-switcher/language-switcher.component';
import { TableComponent } from './core/components/table/table.component';
import { SbbButtonModule } from '@sbb-esta/angular-business';

// AoT requires an exported function for factories
const httpLoaderFactory = (http: HttpClient) => {
  return new TranslateHttpLoader(http);
};

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.backendUrl,
  };
  return new Configuration(params);
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AuthInsightsComponent,
    HeaderComponent,
    LanguageSwitcherComponent,
    UserComponent,
    TableComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MaterialModule,
    ApiModule.forRoot(apiConfigFactory),
    OAuthModule.forRoot({
      resourceServer: {
        // When sendAccessToken is set to true and you send
        // a request to these, the access token is appended.
        // Documentation:
        // https://manfredsteyer.github.io/angular-oauth2-oidc/docs/additional-documentation/working-with-httpinterceptors.html
        allowedUrls: [environment.backendUrl],
        sendAccessToken: true,
      },
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    AppRoutingModule,
    MatToolbarModule,
    MatIconModule,
    MatMenuModule,
    MatListModule,
    MatButtonToggleModule,
    MatButtonModule,
    SbbButtonModule,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
