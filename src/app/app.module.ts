import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OAuthModule } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SbbAngularLibraryModule } from './shared/sbb-angular-library.module';
import { AuthInsightsComponent } from './auth-insights/auth-insights.component';
import { HomeComponent } from './home/home.component';
import { environment } from '../environments/environment';
import { ApiModule, Configuration, ConfigurationParameters } from './api';

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.backendUrl,
  };
  return new Configuration(params);
}

@NgModule({
  declarations: [AppComponent, HomeComponent, AuthInsightsComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    SbbAngularLibraryModule,
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
    AppRoutingModule,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
