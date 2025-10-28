import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ApplicationRole,
  ApplicationType,
  TimetableFieldNumberVersion,
} from '../../../api';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import {
  catchError,
  distinctUntilChanged,
  EMPTY,
  of,
  skipWhile,
  startWith,
} from 'rxjs';
import { Pages } from '../../pages';
import { ValidityService } from '../../sepodi/validity/validity.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';
import { AsyncPipe } from '@angular/common';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../core/form-components/date-range/date-range.component';
import { BusinessOrganisationSelectComponent } from '../../../core/form-components/bo-select/business-organisation-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TimetableFieldNumberDetailFormGroup } from './timetable-field-number-detail-form-group';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import moment from 'moment';
import { MeansOfTransportPickerComponent } from '../../../core/form-components/means-of-transport-picker/means-of-transport-picker.component';
import { SelectionValidator } from '../../../core/validation/min-selected/selection-validator';
import { map, tap } from 'rxjs/operators';
import { required } from '../../../core/util/values';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { ScrollToTopDirective } from '../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContentComponent } from '../../../core/components/detail-page-content/detail-page-content.component';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../core/components/detail-footer/detail-footer.component';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { UserDetailInfoComponent } from '../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import {
  DetailDialogHelperService,
  DetailWithCancelEdit,
} from '../../../core/detail/detail-dialog-helper.service';
import { ValidationService } from '../../../core/validation/validation.service';
import { TtfnMeanOfTransport } from '../../../api/model/ttfnMeanOfTransport';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  providers: [ValidityService],
  imports: [
    ReactiveFormsModule,
    TextFieldComponent,
    DateRangeComponent,
    BusinessOrganisationSelectComponent,
    TranslatePipe,
    MeansOfTransportPickerComponent,
    AsyncPipe,
    DetailPageContainerComponent,
    ScrollToTopDirective,
    DetailPageContentComponent,
    AtlasButtonComponent,
    DetailFooterComponent,
    DateRangeTextComponent,
    SwitchVersionComponent,
    UserDetailInfoComponent,
  ],
})
export class TimetableFieldNumberDetailComponent
  implements DetailWithCancelEdit
{
  // DI
  private readonly permissionService = inject(PermissionService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly timetableFieldNumberInternalService = inject(
    TimetableFieldNumberInternalService
  );
  private readonly timetableFieldNumberService = inject(
    TimetableFieldNumberService
  );
  private readonly notificationService = inject(NotificationService);
  private readonly validityService = inject(ValidityService);
  private readonly detailDialogHelperService = inject(
    DetailDialogHelperService
  );

  // Template variables
  protected readonly isAtLeastSupervisor =
    this.permissionService.isAtLeastSupervisor(ApplicationType.Ttfn);

  protected readonly allowableMeansOfTransport =
    Object.values(TtfnMeanOfTransport);

  protected displayOutwardLine2$ = of(false);
  protected displayOutwardLine3$ = of(false);
  protected displayReturnLine2$ = of(false);
  protected displayReturnLine3$ = of(false);

  protected selectedVersion?: TimetableFieldNumberVersion;
  protected versions: TimetableFieldNumberVersion[] = [];
  protected showSwitch?: boolean;
  protected maxValidity?: DateRange;
  protected selectedVersionIndex?: number;
  protected boSboidRestriction: string[] = [];

  // Interface impl
  isNew: boolean = true;
  form!: FormGroup;

  ngOnInit() {
    const versions = this.readVersions();
    if (versions.length > 0) {
      VersionsHandlingService.sortByValidFrom(versions);
      this.versions = versions.map((version, versionIndex) => ({
        ...version,
        versionNumber: versionIndex + 1,
      }));
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.versions
        );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.isNew = false;
    }
    this.initForm();
    this.showSwitch = VersionsHandlingService.hasMultipleVersions(
      this.versions
    );
    this.initBoSboidRestriction();
  }

  readVersions(): TimetableFieldNumberVersion[] {
    return (
      this.activatedRoute.snapshot.data as {
        timetableFieldNumberDetail: TimetableFieldNumberVersion[];
      }
    ).timetableFieldNumberDetail;
  }

  getDetailHeading(record: TimetableFieldNumberVersion): string {
    return `${record.number} - ${record.descriptionOutwardLine1}`;
  }

  getDetailSubheading(record: TimetableFieldNumberVersion): string {
    return `${record.ttfnid}`;
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
      const permission = this.permissionService.getApplicationUserPermission(
        ApplicationType.Lidi
      );
      if (permission.role === ApplicationRole.Writer) {
        this.boSboidRestriction =
          PermissionService.getSboidRestrictions(permission);
      } else {
        this.boSboidRestriction = [];
      }
    }
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  toggleEdit() {
    const form = required(this.form, 'form is required');
    if (form.enabled) {
      this.detailDialogHelperService.showCancelEditDialog(this);
    } else {
      form.enable();
      this.validityService.initValidity(form);
    }
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      if (this.selectedVersion?.id) {
        this.validityService.updateValidity(this.form);
        this.validityService.validateAndDisableCustom(
          () => this.updateRecord(),
          () => this.form.disable()
        );
      } else {
        this.createRecord();
      }
    }
  }

  updateRecord(): void {
    const id = required(this.selectedVersion?.id, 'id is required');
    const ttfnid = required(this.selectedVersion?.ttfnid, 'ttfnid is required');
    this.form.disable();
    this.timetableFieldNumberService
      .updateVersionWithVersioning(id, this.getPayloadOfForm())
      .pipe(catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.TTFN.path, ttfnid])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.getPayloadOfForm())
      .pipe(catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('TTFN.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.TTFN.path, version.ttfnid])
          .then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const ttfnid = required(this.selectedVersion?.ttfnid, 'ttfnid is required');
    this.timetableFieldNumberInternalService
      .revokeTimetableFieldNumber(ttfnid)
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.REVOKE_SUCCESS');
        this.router
          .navigate([Pages.TTFN.path, ttfnid])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    const ttfnid = required(this.selectedVersion?.ttfnid, 'ttfnid is required');
    this.timetableFieldNumberInternalService
      .deleteVersions(ttfnid)
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
        this.back();
      });
  }

  revoke() {
    this.detailDialogHelperService.confirmWarning(
      {
        message: 'DIALOG.REVOKE',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      },
      () => this.revokeRecord()
    );
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

  getFormGroup(version?: TimetableFieldNumberVersion): FormGroup {
    const formGroup = new FormGroup<TimetableFieldNumberDetailFormGroup>(
      {
        swissTimetableFieldNumber: new FormControl(
          version?.swissTimetableFieldNumber,
          {
            nonNullable: true,
            validators: [
              Validators.required,
              AtlasFieldLengthValidator.length_50,
              AtlasCharsetsValidator.sid4pt,
            ],
          }
        ),
        ttfnid: new FormControl(version?.ttfnid),
        businessOrganisation: new FormControl(version?.businessOrganisation, {
          nonNullable: true,
          validators: [
            Validators.required,
            AtlasFieldLengthValidator.length_50,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        }),
        number: new FormControl(version?.number, {
          nonNullable: true,
          validators: [
            Validators.required,
            AtlasFieldLengthValidator.length_50,
            AtlasCharsetsValidator.numericWithDot,
          ],
        }),
        status: new FormControl(version?.status),
        descriptionOutwardLine1: new FormControl(
          version?.descriptionOutwardLine1,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
              Validators.required,
            ],
          }
        ),
        descriptionOutwardLine2: new FormControl(
          version?.descriptionOutwardLine2,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
            ],
          }
        ),
        descriptionOutwardLine3: new FormControl(
          version?.descriptionOutwardLine3,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
            ],
          }
        ),
        descriptionReturnLine1: new FormControl(
          version?.descriptionReturnLine1,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
            ],
          }
        ),
        descriptionReturnLine2: new FormControl(
          version?.descriptionReturnLine2,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
            ],
          }
        ),
        descriptionReturnLine3: new FormControl(
          version?.descriptionReturnLine3,
          {
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
            ],
          }
        ),
        meanOfTransport: new FormControl(
          version?.meanOfTransport ? [version.meanOfTransport] : [],
          {
            nonNullable: true,
            validators: [
              Validators.required,
              SelectionValidator.requiredSelected(1),
            ],
          }
        ),
        validFrom: new FormControl(
          version?.validFrom ? moment(version.validFrom) : null,
          [Validators.required]
        ),
        validTo: new FormControl(
          version?.validTo ? moment(version.validTo) : null,
          [Validators.required]
        ),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );

    this.displayOutwardLine2$ = this.getDisplayObs(
      formGroup.controls.descriptionOutwardLine1,
      formGroup.controls.descriptionOutwardLine2
    );

    this.displayOutwardLine3$ = this.getDisplayObs(
      formGroup.controls.descriptionOutwardLine2,
      formGroup.controls.descriptionOutwardLine3
    );

    this.displayReturnLine2$ = this.getDisplayObs(
      formGroup.controls.descriptionReturnLine1,
      formGroup.controls.descriptionReturnLine2
    );

    this.displayReturnLine3$ = this.getDisplayObs(
      formGroup.controls.descriptionReturnLine2,
      formGroup.controls.descriptionReturnLine3
    );

    return formGroup;
  }

  private initForm() {
    this.form = this.getFormGroup(this.selectedVersion);
    if (this.selectedVersion) {
      this.form.disable();
    }
  }

  private readonly handleError = () => {
    this.form.enable();
    return EMPTY;
  };

  private getDisplayObs(
    previous: FormControl<string | null | undefined>,
    actual: FormControl<string | null | undefined>
  ) {
    return previous.valueChanges.pipe(
      startWith(previous.value),
      map((val) => (val?.length ?? 0) > 1),
      distinctUntilChanged(),
      skipWhile((val) => !val),
      tap((val) => {
        if (!val) {
          actual.reset(null);
        }
      })
    );
  }

  private getPayloadOfForm() {
    if (!this.form) {
      return {};
    }
    return {
      ...this.form.value,
      meanOfTransport: this.form.value.meanOfTransport[0],
    };
  }
}
