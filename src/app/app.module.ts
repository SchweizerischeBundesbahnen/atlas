import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OAuthModule } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInsightsComponent } from './pages/auth-insights/auth-insights.component';
import { HomeComponent } from './pages/home/home.component';
import { environment } from '../environments/environment';
import { ApiModule, Configuration, ConfigurationParameters } from './api';
import { HeaderComponent } from './core/components/header/header.component';
import { UserComponent } from './core/components/user/user.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MaterialModule } from './core/module/material.module';
import { LanguageSwitcherComponent } from './core/components/language-switcher/language-switcher.component';
import { TableComponent } from './core/components/table/table.component';
import { TimetableFieldNumberDetailComponent } from './pages/timetable-field-number-detail/timetable-field-number-detail.component';
import { DetailWrapperComponent } from './core/components/detail-wrapper/detail-wrapper.component';
import { SideNavComponent } from './core/components/side-nav/side-nav.component';
import { BreadcrumbComponent } from './core/components/breadcrumb/breadcrumb.component';
import { LoadingSpinnerComponent } from './core/components/loading-spinner/loading-spinner.component';
import { DialogComponent } from './core/components/dialog/dialog.component';

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
    DialogComponent,
    AuthInsightsComponent,
    HeaderComponent,
    LoadingSpinnerComponent,
    LanguageSwitcherComponent,
    UserComponent,
    TableComponent,
    TimetableFieldNumberDetailComponent,
    DetailWrapperComponent,
    SideNavComponent,
    BreadcrumbComponent,
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
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
