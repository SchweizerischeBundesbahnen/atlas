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
import { BaseDetailComponent } from '../components/base-detail/base-detail.component';
import { SideNavComponent } from '../components/side-nav/side-nav.component';
import { MaterialModule } from './material.module';
import { RouterModule } from '@angular/router';
import { OAuthModule } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { EmptyToNullDirective } from '../text-input/empty-to-null';
import { TrimInputDirective } from '../text-input/trim-input';
import { SwitchVersionComponent } from '../components/switch-version/switch-version.component';
import { TableFilterComponent } from '../components/table-filter/table-filter.component';
import { ErrorNotificationComponent } from '../notification/error/error-notification.component';
import { WithDefaultValueDirective } from '../text-input/with-default-value.directive';
import { CoverageComponent } from '../components/coverage/coverage.component';
import { RouteToDialogComponent } from '../components/route-to-dialog/route-to-dialog.component';
import { FormModule } from './form.module';
import { RelationComponent } from '../components/relation/relation.component';
import { UserDetailInfoComponent } from '../components/base-detail/user-edit-info/user-detail-info.component';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { WorkflowComponent } from '../workflow/workflow.component';
import { WorkflowFormComponent } from '../workflow/workflow-form/workflow-form.component';
import { WorkflowCheckFormComponent } from '../workflow/workflow-check-form/workflow-check-form.component';
import { FilterTypeGuardPipe } from '../components/table-filter/filter-type-guard.pipe';
import { DisplayDatePipe } from '../pipe/display-date.pipe';
import { AtlasSpacerComponent } from '../components/spacer/atlas-spacer.component';
import { DetailFooterComponent } from '../components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../components/detail-page-container/detail-page-container.component';
import { SelectComponent } from '../form-components/select/select.component';
import { FileUploadComponent } from '../components/file-upload/file-upload.component';
import { FileComponent } from '../components/file-upload/file/file.component';
import { FileSizePipe } from '../components/file-upload/file-size/file-size.pipe';
import { FileDropDirective } from '../components/file-upload/file-drop/file-drop.directive';

const coreComponents = [
  WorkflowFormComponent,
  WorkflowCheckFormComponent,
  WorkflowComponent,
  BaseDetailComponent,
  DetailPageContainerComponent,
  DetailFooterComponent,
  UserDetailInfoComponent,
  AtlasButtonComponent,
  AtlasSpacerComponent,
  SwitchVersionComponent,
  FileUploadComponent,
  FileDropDirective,
  FileComponent,
  FileSizePipe,
  DialogComponent,
  HeaderComponent,
  LanguageSwitcherComponent,
  LoadingSpinnerComponent,
  SideNavComponent,
  TableComponent,
  UserComponent,
  EmptyToNullDirective,
  TrimInputDirective,
  WithDefaultValueDirective,
  TableFilterComponent,
  ErrorNotificationComponent,
  CoverageComponent,
  RouteToDialogComponent,
  RelationComponent,
  DisplayDatePipe,
  FilterTypeGuardPipe,
  SelectComponent,
];

@NgModule({
  declarations: coreComponents,
  imports: [
    CommonModule,
    MaterialModule,
    TranslateModule,
    RouterModule,
    FormModule,
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
  exports: [...coreComponents, CommonModule, MaterialModule, TranslateModule],
  providers: [TranslatePipe],
})
export class CoreModule {}
