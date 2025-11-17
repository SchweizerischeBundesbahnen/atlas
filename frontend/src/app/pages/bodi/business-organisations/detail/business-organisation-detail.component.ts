import { Component, OnInit } from '@angular/core';
import { BusinessOrganisationVersion, BusinessType } from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { catchError, EMPTY } from 'rxjs';
import { Pages } from '../../../pages';
import {
  BusinessOrganisationDetailFormGroup,
  BusinessOrganisationDetailFormGroupBuilder,
} from './business-organisation-detail-form-group';
import { BusinessOrganisationLanguageService } from '../../../../core/form-components/bo-select/business-organisation-language.service';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { NgIf } from '@angular/common';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { BusinessOrganisationInternalService } from '../../../../api/service/bodi/business-organisation-internal.service';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../../core/versioning/date-range';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { ValidationService } from '../../../../core/validation/validation.service';
import {
  DetailDialogHelperService,
  DetailWithCancelEdit,
} from '../../../../core/detail/detail-dialog-helper.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { UserDetailInfoComponent } from '../../../../core/components/user-edit-info/user-detail-info.component';

@Component({
  templateUrl: './business-organisation-detail.component.html',
  styleUrls: ['./business-organisation-detail.component.scss'],
  providers: [ValidityService, TranslatePipe],
  imports: [
    ReactiveFormsModule,
    NgIf,
    TextFieldComponent,
    DateRangeComponent,
    SelectComponent,
    TranslatePipe,
    DateRangeTextComponent,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ScrollToTopDirective,
    SwitchVersionComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    UserDetailInfoComponent,
  ],
})
export class BusinessOrganisationDetailComponent
  implements OnInit, DetailFormComponent, DetailWithCancelEdit
{
  BUSINESS_TYPES = Object.values(BusinessType);
  versions!: BusinessOrganisationVersion[];
  selectedVersion!: BusinessOrganisationVersion;
  maxValidity!: DateRange;

  form!: FormGroup<BusinessOrganisationDetailFormGroup>;
  isNew = false;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  selectedVersionIndex!: number;

  constructor(
    private readonly businessOrganisationInternalService: BusinessOrganisationInternalService,
    private readonly businessOrganisationLanguageService: BusinessOrganisationLanguageService,
    private readonly router: Router,
    private readonly notificationService: NotificationService,
    private readonly dialogService: DialogService,
    private readonly permissionService: PermissionService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly validityService: ValidityService,
    private readonly detailHelperService: DetailDialogHelperService
  ) {}

  ngOnInit() {
    this.versions =
      this.activatedRoute.snapshot.data.businessOrganisationDetail;
    if (this.versions.length == 0) {
      this.isNew = true;
      this.form = BusinessOrganisationDetailFormGroupBuilder.getFormGroup();
    } else {
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.versions
        );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
      this.initSelectedVersion();
    }
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(
      this.versions
    );
    this.form = BusinessOrganisationDetailFormGroupBuilder.getFormGroup(
      this.selectedVersion
    );
    if (!this.isNew) {
      this.form.disable();
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.isSwitchVersionDisabled = true;
      this.validityService.initValidity(this.form);
      this.form.enable();
    }
  }

  displayedAbbreviation() {
    return this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.versions[newIndex];
    this.initSelectedVersion();
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const sublineVersion =
        this.form.getRawValue() as unknown as BusinessOrganisationVersion;
      this.form.disable();
      if (this.isNew) {
        this.create(sublineVersion);
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validate().subscribe((confirmed) => {
          if (confirmed) {
            this.form.disable();
            this.update(this.selectedVersion.id!, sublineVersion);
          }
        });
      }
    }
  }

  update(
    id: number,
    businessOrganisationVersion: BusinessOrganisationVersion
  ): void {
    this.businessOrganisationInternalService
      .updateBusinessOrganisationVersion(id, businessOrganisationVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success(
          'BODI.BUSINESS_ORGANISATION.NOTIFICATION.EDIT_SUCCESS'
        );
        this.router
          .navigate([
            Pages.BODI.path,
            Pages.BUSINESS_ORGANISATIONS.path,
            this.selectedVersion.sboid,
          ])
          .then(() => this.ngOnInit());
      });
  }

  create(businessOrganisationVersion: BusinessOrganisationVersion): void {
    this.form.disable();
    this.businessOrganisationInternalService
      .createBusinessOrganisationVersion(businessOrganisationVersion)
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success(
          'BODI.BUSINESS_ORGANISATION.NOTIFICATION.ADD_SUCCESS'
        );
        this.router
          .navigate([
            Pages.BODI.path,
            Pages.BUSINESS_ORGANISATIONS.path,
            version.sboid,
          ])
          .then(() => this.ngOnInit());
      });
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  revoke(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.sboid) {
            this.businessOrganisationInternalService
              .revokeBusinessOrganisation(this.selectedVersion.sboid)
              .subscribe(() => {
                this.notificationService.success(
                  'BODI.BUSINESS_ORGANISATION.NOTIFICATION.REVOKE_SUCCESS'
                );
                this.router
                  .navigate([
                    Pages.BODI.path,
                    Pages.BUSINESS_ORGANISATIONS.path,
                    this.selectedVersion.sboid,
                  ])
                  .then(() => this.ngOnInit());
              });
          }
        }
      });
  }

  delete(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.DELETE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_DELETE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.sboid != null) {
            this.businessOrganisationInternalService
              .deleteBusinessOrganisation(this.selectedVersion.sboid)
              .subscribe(() => {
                this.notificationService.success(
                  'BODI.BUSINESS_ORGANISATION.NOTIFICATION.DELETE_SUCCESS'
                );
                this.back();
              });
          }
        }
      });
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }
}
