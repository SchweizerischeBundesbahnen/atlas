import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Status, TimetableFieldNumbersService, TimetableFieldNumberVersion } from '../../../api';
import { DetailWrapperController } from '../../../core/components/detail-wrapper/detail-wrapper-controller';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import { catchError, EMPTY, Subject } from 'rxjs';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { takeUntil } from 'rxjs/operators';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Pages } from '../../pages';
import {
  DateService,
  MAX_DATE,
  MAX_DATE_FORMATTED,
  MIN_DATE,
} from '../../../core/date/date.service';
import { Page } from '../../../core/model/page';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
})
export class TimetableFieldNumberDetailComponent
  extends DetailWrapperController<TimetableFieldNumberVersion>
  implements OnInit, OnDestroy
{
  SWISS_TIMETABLE_FIELD_NUMBER_PLACEHOLDER = 'bO.BEX:a';
  VALID_TO_PLACEHOLDER = MAX_DATE_FORMATTED;
  DESCRIPTION_PLACEHOLDER = 'Grenze - Bad, Bahnhof - Basel SBB - Zürich HB - Chur';

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  readonly STATUS_OPTIONS = Object.values(Status);

  private ngUnsubscribe = new Subject<void>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    private formBuilder: FormBuilder,
    protected notificationService: NotificationService,
    protected dialogService: DialogService,
    private dateService: DateService
  ) {
    super(dialogService, notificationService);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecord(): TimetableFieldNumberVersion {
    return this.dialogData.timetableFieldNumberDetail;
  }

  getTitle(record: TimetableFieldNumberVersion): string | undefined {
    return record.swissTimetableFieldNumber;
  }

  updateRecord(): void {
    this.timetableFieldNumberService
      .updateVersionWithVersioning(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.EDIT_SUCCESS');
        this.router.navigate([Pages.TTFN.path, this.record.ttfnid]).then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('TTFN.NOTIFICATION.ADD_SUCCESS');
        this.router.navigate([Pages.TTFN.path, version.ttfnid]).then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    const selectedRecord: TimetableFieldNumberVersion = this.getSelectedRecord();
    if (selectedRecord.ttfnid != null) {
      this.timetableFieldNumberService
        .deleteVersions(selectedRecord.ttfnid)
        .pipe(
          takeUntil(this.ngUnsubscribe),
          catchError((err) => {
            this.notificationService.error(err, 'TTFN.NOTIFICATION.DELETE_ERROR');
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
          this.backToOverview();
        });
    }
  }

  backToOverview(): void {
    this.router.navigate([Pages.TTFN.path]).then();
  }

  getFormGroup(version: TimetableFieldNumberVersion): FormGroup {
    return this.formBuilder.group(
      {
        swissTimetableFieldNumber: [
          version.swissTimetableFieldNumber,
          [Validators.required, AtlasFieldLengthValidator.small, AtlasCharsetsValidator.sid4pt],
        ],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
        ttfnid: version.ttfnid,
        businessOrganisation: [
          version.businessOrganisation,
          [
            Validators.required,
            AtlasFieldLengthValidator.small,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        number: [
          version.number,
          [
            Validators.required,
            AtlasFieldLengthValidator.small,
            AtlasCharsetsValidator.numericWithDot,
          ],
        ],
        description: [
          version.description,
          [
            AtlasFieldLengthValidator.mid,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        comment: [
          version.comment,
          [
            AtlasFieldLengthValidator.comments,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        status: version.status,
        etagVersion: version.etagVersion,
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  getValidFromPlaceHolder() {
    return this.dateService.getCurrentDateFormatted();
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  getPageType(): Page {
    return Pages.TTFN;
  }
}
