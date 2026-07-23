import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationRole, ApplicationType, MeanOfTransport, TimetableFieldNumberVersion } from '../../../api';
import { ReactiveFormsModule } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import { catchError, EMPTY } from 'rxjs';
import { Pages } from '../../pages';
import { ValidityService } from '../../sepodi/validity/validity.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';
import { TranslatePipe } from '@ngx-translate/core';
import {
  DESCRIPTION_MAX_LENGTH,
  NUMBER_MAX_LENGTH,
  TimetableFieldNumberDetailForm,
  TimetableFieldNumberDetailFormModel,
} from './timetable-field-number-detail-form-group';
import { required } from '../../../core/util/values';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { ScrollToTopDirective } from '../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContentComponent } from '../../../core/components/detail-page-content/detail-page-content.component';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../core/components/detail-footer/detail-footer.component';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { UserDetailInfoComponent } from '../../../core/components/user-edit-info/user-detail-info.component';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import {
  DetailDialogHelperService,
  SignalDetailWithCancelEdit,
} from '../../../core/detail/detail-dialog-helper.service';
import { TtfnMeanOfTransport } from '../../../api/model/ttfnMeanOfTransport';
import { Revokable, RevokeButton } from '../../../core/form-components/revoke-button/revoke-button';
import { DetailFormComponent } from '../../../core/leave-guard/leave-dirty-form-guard.service';
import { apply, disabled, Field, form, Schema, schema } from '@angular/forms/signals';
import { FormValidators } from '../../../core/validation/form-validators.service';
import { AtlasTextFieldComponent } from '@atlas/form';
import { AtlasBoSelectComponent } from '../../../core/form-components/atlas-bo-select/atlas-bo-select.component';
import { AtlasDateRangeComponent } from '../../../core/form-components/atlas-date-range/atlas-date-range.component';
import { AtlasMeansOfTransportPickerComponent } from '../../../core/form-components/atlas-means-of-transport-picker/atlas-means-of-transport-picker.component';

@Component({
  selector: 'atlas-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  providers: [ValidityService],
  imports: [
    ReactiveFormsModule,
    TranslatePipe,
    AtlasTextFieldComponent,
    AtlasBoSelectComponent,
    AtlasDateRangeComponent,
    AtlasMeansOfTransportPickerComponent,
    DetailPageContainerComponent,
    ScrollToTopDirective,
    DetailPageContentComponent,
    AtlasButtonComponent,
    DetailFooterComponent,
    DateRangeTextComponent,
    SwitchVersionComponent,
    UserDetailInfoComponent,
    RevokeButton,
  ],
})
export class TimetableFieldNumberDetailComponent
  implements SignalDetailWithCancelEdit<TimetableFieldNumberDetailFormModel>, Revokable, DetailFormComponent, OnInit
{
  // Interface impl
  isNew = true;
  readonly emptyFormValue = TimetableFieldNumberDetailForm.emptyFormValue;
  protected readonly descriptionMaxChars = String(DESCRIPTION_MAX_LENGTH);
  protected readonly allowableMeansOfTransport = Object.values(TtfnMeanOfTransport) as unknown as MeanOfTransport[];

  protected selectedVersion?: TimetableFieldNumberVersion;
  protected versions: TimetableFieldNumberVersion[] = [];
  protected showSwitch = false;
  protected maxValidity?: DateRange;
  protected selectedVersionIndex?: number;
  protected boSboidRestriction: string[] = [];

  // DI
  private readonly permissionService = inject(PermissionService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly timetableFieldNumberInternalService = inject(TimetableFieldNumberInternalService);
  private readonly timetableFieldNumberService = inject(TimetableFieldNumberService);
  private readonly notificationService = inject(NotificationService);
  private readonly validityService = inject(ValidityService);
  private readonly detailDialogHelperService = inject(DetailDialogHelperService);
  private readonly formValidators = inject(FormValidators);

  // Template variables
  protected readonly isAtLeastSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Ttfn);

  readonly editMode = signal(false);
  readonly formModel = signal<TimetableFieldNumberDetailFormModel>(this.emptyFormValue);
  readonly timetableFieldNumberForm = form(this.formModel, (schemaPath) => {
    disabled(schemaPath, {
      when: () => !this.editMode(),
    });

    const descriptionSchema: Schema<string> = schema((field) => {
      this.formValidators.maxLength(field, DESCRIPTION_MAX_LENGTH);
      this.formValidators.blankOrEmptySpaceSurrounding(field);
    });

    this.formValidators.required(schemaPath.number);
    this.formValidators.ttfnNumber(schemaPath.number);
    this.formValidators.maxLength(schemaPath.number, NUMBER_MAX_LENGTH);

    this.formValidators.required(schemaPath.businessOrganisation);

    this.formValidators.required(schemaPath.descriptionOutwardLine1);
    apply(schemaPath.descriptionOutwardLine1, descriptionSchema);
    apply(schemaPath.descriptionOutwardLine2, descriptionSchema);
    apply(schemaPath.descriptionOutwardLine3, descriptionSchema);
    apply(schemaPath.descriptionReturnLine1, descriptionSchema);
    apply(schemaPath.descriptionReturnLine2, descriptionSchema);
    apply(schemaPath.descriptionReturnLine3, descriptionSchema);

    this.formValidators.atLeastOneSelected(schemaPath.meanOfTransport);

    this.formValidators.required(schemaPath.validFrom);
    this.formValidators.required(schemaPath.validTo);
    this.formValidators.validToAfterOrEqualValidFrom(schemaPath);
  });

  readonly dirty = computed(() => this.timetableFieldNumberForm().dirty());

  protected readonly displayOutwardLine2 = computed(() =>
    this.hasContent(this.timetableFieldNumberForm.descriptionOutwardLine1)
  );
  protected readonly displayOutwardLine3 = computed(() =>
    this.hasContent(this.timetableFieldNumberForm.descriptionOutwardLine2)
  );
  protected readonly displayReturnLine2 = computed(() =>
    this.hasContent(this.timetableFieldNumberForm.descriptionReturnLine1)
  );
  protected readonly displayReturnLine3 = computed(() =>
    this.hasContent(this.timetableFieldNumberForm.descriptionReturnLine2)
  );

  constructor() {
    // Clear the follow-up description lines once their preceding line no longer holds content while editing.
    effect(() => {
      if (this.editMode()) {
        this.resetIfHidden(this.displayOutwardLine2(), this.timetableFieldNumberForm.descriptionOutwardLine2);
        this.resetIfHidden(this.displayOutwardLine3(), this.timetableFieldNumberForm.descriptionOutwardLine3);
        this.resetIfHidden(this.displayReturnLine2(), this.timetableFieldNumberForm.descriptionReturnLine2);
        this.resetIfHidden(this.displayReturnLine3(), this.timetableFieldNumberForm.descriptionReturnLine3);
      }
    });
  }

  ngOnInit() {
    const versions = this.readVersions();
    if (versions.length > 0) {
      VersionsHandlingService.sortByValidFrom(versions);
      this.versions = versions.map((version, versionIndex) => ({
        ...version,
        versionNumber: versionIndex + 1,
      }));
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(this.versions);
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.isNew = false;
    } else {
      this.isNew = true;
    }
    this.initForm();
    this.showSwitch = VersionsHandlingService.hasMultipleVersions(this.versions);
    this.initBoSboidRestriction();
  }

  readVersions(): TimetableFieldNumberVersion[] {
    return (
      this.activatedRoute.snapshot.data as {
        timetableFieldNumberDetail: TimetableFieldNumberVersion[];
      }
    ).timetableFieldNumberDetail;
  }

  switchVersion(index: number) {
    this.selectedVersionIndex = index;
    this.selectedVersion = this.versions[index];
    this.initForm();
  }

  initBoSboidRestriction() {
    if (this.selectedVersion || this.permissionService.isAdmin) {
      this.boSboidRestriction = [];
    } else {
      const permission = this.permissionService.getApplicationUserPermission(ApplicationType.Lidi);
      if (permission.role === ApplicationRole.Writer) {
        this.boSboidRestriction = PermissionService.getSboidRestrictions(permission);
      } else {
        this.boSboidRestriction = [];
      }
    }
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  toggleEdit() {
    if (this.editMode()) {
      this.detailDialogHelperService.openCancelEditDialog(this);
    } else {
      this.validityService.init(this.formModel());
      this.editMode.set(true);
    }
  }

  save() {
    this.timetableFieldNumberForm().markAsTouched();
    if (this.timetableFieldNumberForm().invalid()) {
      return;
    }

    if (this.selectedVersion?.id) {
      this.validityService.update(this.formModel());
      this.validityService.validate().subscribe((confirmed) => {
        if (confirmed) {
          this.editMode.set(false);
          this.updateRecord();
        }
      });
    } else {
      this.createRecord();
    }
  }

  updateRecord(): void {
    const id = required(this.selectedVersion?.id, 'id is required');
    const ttfnid = required(this.selectedVersion?.ttfnid, 'ttfnid is required');
    this.timetableFieldNumberService
      .updateVersionWithVersioning(id, TimetableFieldNumberDetailForm.toApiModel(this.formModel()))
      .pipe(catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.EDIT_SUCCESS');
        this.router.navigate([Pages.TTFN.path, ttfnid]).then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(TimetableFieldNumberDetailForm.toApiModel(this.formModel()))
      .pipe(catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('TTFN.NOTIFICATION.ADD_SUCCESS');
        this.router.navigate([Pages.TTFN.path, version.ttfnid]).then(() => this.ngOnInit());
      });
  }

  revoke(): void {
    const ttfnid = required(this.selectedVersion?.ttfnid, 'ttfnid is required');
    this.timetableFieldNumberInternalService.revokeTimetableFieldNumber(ttfnid).subscribe(() => {
      this.notificationService.success('TTFN.NOTIFICATION.REVOKE_SUCCESS');
      this.router.navigate([Pages.TTFN.path, ttfnid]).then(() => this.ngOnInit());
    });
  }

  deleteRecord(): void {
    const ttfnid = required(this.selectedVersion?.ttfnid, 'ttfnid is required');
    this.timetableFieldNumberInternalService.deleteVersions(ttfnid).subscribe(() => {
      this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
      this.back();
    });
  }

  delete() {
    this.detailDialogHelperService.confirmWarning(
      {
        message: 'DIALOG.DELETE',
        confirmText: 'DIALOG.CONFIRM_DELETE',
      },
      () => this.deleteRecord()
    );
  }

  private initForm() {
    if (this.selectedVersion) {
      this.timetableFieldNumberForm().reset(TimetableFieldNumberDetailForm.toFormModel(this.selectedVersion));
      this.editMode.set(false);
    } else {
      this.timetableFieldNumberForm().reset(this.emptyFormValue);
      this.editMode.set(true);
    }
  }

  private hasContent(field: Field<string>): boolean {
    return (field().value()?.length ?? 0) > 1;
  }

  private resetIfHidden(visible: boolean, field: Field<string>) {
    if (!visible && field().value()) {
      field().value.set('');
    }
  }

  private readonly handleError = () => {
    this.editMode.set(true);
    return EMPTY;
  };
}
