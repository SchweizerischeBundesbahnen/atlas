import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { HttpClientModule } from '@angular/common/http';
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
import { OAuthModule } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { EmptyToNullDirective } from '../text-input/empty-to-null';
import { TrimInputDirective } from '../text-input/trim-input';
import { SwitchVersionComponent } from '../components/switch-version/switch-version.component';
import { TableSearchComponent } from '../components/table-search/table-search.component';
import { ErrorNotificationComponent } from '../notification/error-notification.component';
import { VersionedElementComponent } from '../components/versioned-element/versioned-element.component';

@NgModule({
  declarations: [
    BreadcrumbComponent,
    DetailWrapperComponent,
    SwitchVersionComponent,
    VersionedElementComponent,
    DialogComponent,
    HeaderComponent,
    LanguageSwitcherComponent,
    LoadingSpinnerComponent,
    SideNavComponent,
    TableComponent,
    UserComponent,
    EmptyToNullDirective,
    TrimInputDirective,
    TableSearchComponent,
    ErrorNotificationComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    TranslateModule,
    RouterModule,
    HttpClientModule,
    OAuthModule.forRoot({
      resourceServer: {
        // When sendAccessToken is set to true and you send
        // a request to these, the access token is appended.
        // Documentation:
        // https://manfredsteyer.github.io/angular-oauth2-oidc/docs/additional-documentation/working-with-httpinterceptors.html
        allowedUrls: [environment.atlasApiUrl],
        sendAccessToken: true,
      },
    }),
  ],
  exports: [
    BreadcrumbComponent,
    DetailWrapperComponent,
    SwitchVersionComponent,
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
    EmptyToNullDirective,
    TrimInputDirective,
    TableSearchComponent,
    ErrorNotificationComponent,
  ],
  providers: [TranslatePipe],
})
export class CoreModule {}
