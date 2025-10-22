import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationType, TimetableFieldNumberVersion } from '../../../api';
import { BaseDetailController } from '../../../core/components/base-detail/base-detail-controller';
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
  of,
  skipWhile,
  startWith,
} from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Pages } from '../../pages';
import { Page } from '../../../core/model/page';
import { ValidityService } from '../../sepodi/validity/validity.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';
import { BaseDetailComponent } from '../../../core/components/base-detail/base-detail.component';
import { AsyncPipe, NgIf } from '@angular/common';
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

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  providers: [ValidityService],
  imports: [
    BaseDetailComponent,
    ReactiveFormsModule,
    NgIf,
    TextFieldComponent,
    DateRangeComponent,
    BusinessOrganisationSelectComponent,
    TranslatePipe,
    MeansOfTransportPickerComponent,
    AsyncPipe,
  ],
})
export class TimetableFieldNumberDetailComponent
  extends BaseDetailController<TimetableFieldNumberVersion>
  implements OnInit
{
  protected allowableMeansOfTransport = Object.values(
    TimetableFieldNumberVersion.MeanOfTransportEnum
  );
  protected displayOutwardLine2$ = of(false);
  protected displayOutwardLine3$ = of(false);
  protected displayReturnLine2$ = of(false);
  protected displayReturnLine3$ = of(false);

  constructor(
    protected router: Router,
    private timetableFieldNumberInternalService: TimetableFieldNumberInternalService,
    private timetableFieldNumberService: TimetableFieldNumberService,
    protected notificationService: NotificationService,
    protected dialogService: DialogService,
    protected permissionService: PermissionService,
    protected activatedRoute: ActivatedRoute,
    protected validityService: ValidityService
  ) {
    super(
      router,
      dialogService,
      notificationService,
      permissionService,
      activatedRoute,
      validityService
    );
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecords(): TimetableFieldNumberVersion[] {
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

  updateRecord(): void {
    const id = this.getId();
    const ttfnid = this.record?.ttfnid;
    if (!id || !ttfnid) throw new Error('id and ttfnid are required');
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
    const ttfnid = this.getSelectedRecord()?.ttfnid;
    if (!ttfnid) throw new Error('ttfnid is required');
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
    const ttfnid = this.getSelectedRecord()?.ttfnid;
    if (!ttfnid) throw new Error('ttfnid is required');
    this.timetableFieldNumberInternalService
      .deleteVersions(ttfnid)
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
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

  getPageType(): Page {
    return Pages.TTFN;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Ttfn;
  }

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
    return {
      ...this.form.value,
      meanOfTransport: this.form.value.meanOfTransport[0],
    };
  }
}
