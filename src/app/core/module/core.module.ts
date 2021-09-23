import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { DialogComponent } from '../components/dialog/dialog.component';
import { HeaderComponent } from '../components/header/header.component';
import { LoadingSpinnerComponent } from '../components/loading-spinner/loading-spinner.component';
import { LanguageSwitcherComponent } from '../components/language-switcher/language-switcher.component';
import { UserComponent } from '../components/user/user.component';
import { TableComponent } from '../components/table/table.component';
import { DetailWrapperComponent } from '../components/detail-wrapper/detail-wrapper.component';
import { SideNavComponent } from '../components/side-nav/side-nav.component';
import { BreadcrumbComponent } from '../components/breadcrumb/breadcrumb.component';
import { MaterialModule } from './material.module';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ApiModule, Configuration, ConfigurationParameters } from '../../api';
import { OAuthModule } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';

// AoT requires an exported function for factories
const httpLoaderFactory = (http: HttpClient) => {
  return new TranslateHttpLoader(http);
};

function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.backendUrl,
  };
  return new Configuration(params);
}

@NgModule({
  declarations: [
    BreadcrumbComponent,
    DetailWrapperComponent,
    DialogComponent,
    HeaderComponent,
    LanguageSwitcherComponent,
    LoadingSpinnerComponent,
    SideNavComponent,
    TableComponent,
    UserComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    RouterModule,
    BrowserAnimationsModule,
    HttpClientModule,
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
  ],
  exports: [
    BreadcrumbComponent,
    DetailWrapperComponent,
    DialogComponent,
    HeaderComponent,
    LanguageSwitcherComponent,
    LoadingSpinnerComponent,
    SideNavComponent,
    TableComponent,
    UserComponent,
    CommonModule,
    MaterialModule,
    TranslateModule,
  ],
})
export class CoreModule {}
