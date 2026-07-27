import { Component, computed, inject, OnInit, Signal, signal } from '@angular/core';
import { BusinessOrganisationVersion, BusinessType } from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { catchError, EMPTY } from 'rxjs';
import { Pages } from '../../../pages';
import { BusinessOrganisationLanguageService } from '../shared/business-organisation-language.service';
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
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { UserDetailInfoComponent } from '../../../../core/components/user-edit-info/user-detail-info.component';
import { Revokable, RevokeButton } from '../../../../core/form-components/revoke-button/revoke-button';
import { AtlasLabelFieldComponent, AtlasSelectComponent, AtlasTextFieldComponent } from '@atlas/form';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableComponent } from '../../../../core/components/table/table.component';
import { map } from 'rxjs/operators';
import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { AtlasDateRangeComponent } from '../../../../core/form-components/atlas-date-range/atlas-date-range.component';
import { apply, disabled, form, Schema, schema } from '@angular/forms/signals';
import { FormValidators } from '../../../../core/validation/form-validators.service';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';
import {
  BusinessOrganisationDetailForm,
  BusinessOrganisationDetailFormModel,
} from './business-organisation-detail-form-group';

type TransportCompanyRelationTableEntry = {
  abbreviation?: string;
  businessRegisterName?: string;
  validFrom?: Date;
  validTo?: Date;
  transportCompanyId?: number;
};

@Component({
  templateUrl: './business-organisation-detail.component.html',
  styleUrls: ['./business-organisation-detail.component.scss'],
  providers: [ValidityService, TranslatePipe],
  imports: [
    ReactiveFormsModule,
    AtlasTextFieldComponent,
    AtlasDateRangeComponent,
    AtlasSelectComponent,
    TranslatePipe,
    DateRangeTextComponent,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ScrollToTopDirective,
    SwitchVersionComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    UserDetailInfoComponent,
    RevokeButton,
    AtlasLabelFieldComponent,
    TableComponent,
  ],
})
export class BusinessOrganisationDetailComponent implements Revokable, OnInit, DetailFormComponent {
  private readonly businessOrganisationInternalService = inject(BusinessOrganisationInternalService);
  private readonly businessOrganisationLanguageService = inject(BusinessOrganisationLanguageService);
  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogService = inject(DialogService);
  private readonly validityService = inject(ValidityService);
  private readonly detailHelperService = inject(DetailDialogHelperService);
  private readonly formValidators = inject(FormValidators);

  BUSINESS_TYPES = Object.values(BusinessType);
  versions!: BusinessOrganisationVersion[];
  selectedVersion!: BusinessOrganisationVersion;
  maxValidity!: DateRange;

  readonly emptyFormValue = BusinessOrganisationDetailForm.emptyFormValue;
  readonly editMode = signal(false);
  readonly formModel = signal<BusinessOrganisationDetailFormModel>(this.emptyFormValue);
  readonly businessOrganisationForm = form(this.formModel, (schemaPath) => {
    disabled(schemaPath, {
      when: () => !this.editMode(),
    });

    const abbreviationSchema: Schema<string> = schema((field) => {
      this.formValidators.required(field);
      this.formValidators.maxLength(field, 10);
      this.formValidators.iso88591(field);
    });
    const descriptionSchema: Schema<string> = schema((field) => {
      this.formValidators.required(field);
      this.formValidators.maxLength(field, 60);
      this.formValidators.iso88591(field);
      this.formValidators.blankOrEmptySpaceSurrounding(field);
    });

    apply(schemaPath.descriptionDe, descriptionSchema);
    apply(schemaPath.descriptionFr, descriptionSchema);
    apply(schemaPath.descriptionIt, descriptionSchema);
    apply(schemaPath.descriptionEn, descriptionSchema);

    apply(schemaPath.abbreviationDe, abbreviationSchema);
    apply(schemaPath.abbreviationFr, abbreviationSchema);
    apply(schemaPath.abbreviationIt, abbreviationSchema);
    apply(schemaPath.abbreviationEn, abbreviationSchema);

    this.formValidators.required(schemaPath.organisationNumber);
    this.formValidators.numeric(schemaPath.organisationNumber);
    this.formValidators.maxLength(schemaPath.organisationNumber, 5);

    this.formValidators.maxLength(schemaPath.contactEnterpriseEmail, 255);
    this.formValidators.email(schemaPath.contactEnterpriseEmail);

    this.formValidators.required(schemaPath.validFrom);
    this.formValidators.required(schemaPath.validTo);
    this.formValidators.validToAfterOrEqualValidFrom(schemaPath);
  });

  dirty = computed(() => this.businessOrganisationForm().dirty());

  isNew = false;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  selectedVersionIndex!: number;

  protected readonly tcRelationColumns: TableColumn<TransportCompanyRelationTableEntry>[] = [
    {
      headerTitle: 'BODI.TRANSPORT_COMPANIES.ABBREVIATION',
      value: 'abbreviation',
    },
    {
      headerTitle: 'BODI.TRANSPORT_COMPANIES.BUSINESS_REGISTER_NAME',
      value: 'businessRegisterName',
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    {
      headerTitle: 'COMMON.VALID_TO',
      value: 'validTo',
      formatAsDate: true,
    },
  ];

  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly transportCompanyRelationInternalService = inject(TransportCompanyRelationInternalService);
  private readonly getSboid = (): string | undefined =>
    this.activatedRoute.snapshot.data.businessOrganisationDetail[0]?.sboid;
  private readonly getTcRelations = () => {
    const sboid = this.getSboid();
    return sboid ? this.transportCompanyRelationInternalService.getBoTransportCompanyRelations(sboid) : EMPTY;
  };
  protected readonly tcRelations: Signal<TransportCompanyRelationTableEntry[]> = toSignal(
    this.getTcRelations().pipe(
      map((relations) =>
        relations.map((rel): TransportCompanyRelationTableEntry => ({
          abbreviation: rel.transportCompany?.abbreviation,
          businessRegisterName: rel.transportCompany?.businessRegisterName,
          validFrom: rel.validFrom,
          validTo: rel.validTo,
          transportCompanyId: rel.transportCompany?.id,
        }))
      )
    ),
    {
      initialValue: [],
    }
  );

  ngOnInit() {
    this.versions = this.activatedRoute.snapshot.data.businessOrganisationDetail;
    if (this.versions.length == 0) {
      this.isNew = true;
      this.businessOrganisationForm().reset(this.emptyFormValue);
      this.editMode.set(true);
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(this.versions);
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
      this.initSelectedVersion();
    }
  }

  toggleEdit() {
    if (this.editMode()) {
      this.detailHelperService.openCancelEditDialog(this);
    } else {
      this.isSwitchVersionDisabled = true;
      this.validityService.init(this.formModel());
      this.editMode.set(true);
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
    this.businessOrganisationForm().markAsTouched();
    if (this.businessOrganisationForm().invalid()) {
      return;
    }

    const businessOrganisationVersion = BusinessOrganisationDetailForm.toApiModel(this.formModel());
    if (this.isNew) {
      this.create(businessOrganisationVersion);
    } else {
      this.validityService.update(this.formModel());
      this.validityService.validate().subscribe((confirmed) => {
        if (confirmed) {
          this.editMode.set(false);
          this.update(this.selectedVersion.id!, businessOrganisationVersion);
        }
      });
    }
  }

  update(id: number, businessOrganisationVersion: BusinessOrganisationVersion): void {
    this.businessOrganisationInternalService
      .updateBusinessOrganisationVersion(id, businessOrganisationVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('BODI.BUSINESS_ORGANISATION.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, this.selectedVersion.sboid])
          .then(() => this.ngOnInit());
      });
  }

  create(businessOrganisationVersion: BusinessOrganisationVersion): void {
    this.businessOrganisationInternalService
      .createBusinessOrganisationVersion(businessOrganisationVersion)
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('BODI.BUSINESS_ORGANISATION.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, version.sboid])
          .then(() => this.ngOnInit());
      });
  }

  revoke(): void {
    this.businessOrganisationInternalService.revokeBusinessOrganisation(this.selectedVersion.sboid!).subscribe(() => {
      this.notificationService.success('BODI.BUSINESS_ORGANISATION.NOTIFICATION.REVOKE_SUCCESS');
      this.router
        .navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, this.selectedVersion.sboid])
        .then(() => this.ngOnInit());
    });
  }

  delete(): void {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.DELETE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_DELETE',
      } satisfies DialogData)
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.sboid != null) {
            this.businessOrganisationInternalService
              .deleteBusinessOrganisation(this.selectedVersion.sboid)
              .subscribe(() => {
                this.notificationService.success('BODI.BUSINESS_ORGANISATION.NOTIFICATION.DELETE_SUCCESS');
                this.back();
              });
          }
        }
      });
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  openInNewTab(transportCompanyId?: number) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.BODI.path, Pages.TRANSPORT_COMPANIES.path, transportCompanyId])
    );
    window.open(url, '_blank');
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.versions);
    this.businessOrganisationForm().reset(BusinessOrganisationDetailForm.toFormModel(this.selectedVersion));
    if (!this.isNew) {
      this.editMode.set(false);
    }
  }

  private handleError() {
    return () => {
      this.editMode.set(true);
      return EMPTY;
    };
  }
}
