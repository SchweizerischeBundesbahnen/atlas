import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ApplicationType,
  TimetableFieldNumbersService,
  TimetableFieldNumberVersion,
} from '../../../api';
import { BaseDetailController } from '../../../core/components/base-detail/base-detail-controller';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import { catchError, Subject } from 'rxjs';
import moment from 'moment';
import { DateRangeValidator } from '../../../core/validation/date-range/date-range-validator';
import { takeUntil } from 'rxjs/operators';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Pages } from '../../pages';
import { Page } from '../../../core/model/page';
import { AtlasCharsetsValidator } from '../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../core/validation/whitespace/whitespace-validator';
import { AtlasFieldLengthValidator } from '../../../core/validation/field-lengths/atlas-field-length-validator';
import { TimetableFieldNumberDetailFormGroup } from './timetable-field-number-detail-form-group';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent
  extends BaseDetailController<TimetableFieldNumberVersion>
  implements OnInit, OnDestroy
{
  private ngUnsubscribe = new Subject<void>();

  constructor(
    protected router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    protected notificationService: NotificationService,
    protected dialogService: DialogService,
    protected authService: AuthService,
    protected activatedRoute: ActivatedRoute,
  ) {
    super(router, dialogService, notificationService, authService, activatedRoute);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecord(): TimetableFieldNumberVersion {
    return this.activatedRoute.snapshot.data.timetableFieldNumberDetail;
  }

  getDetailHeading(record: TimetableFieldNumberVersion): string {
    return `${record.number} - ${record.description ?? ''}`;
  }

  getDetailSubheading(record: TimetableFieldNumberVersion): string {
    return `${record.ttfnid}`;
  }

  updateRecord(): void {
    this.timetableFieldNumberService
      .updateVersionWithVersioning(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.EDIT_SUCCESS');
        this.router.navigate([Pages.TTFN.path, this.record.ttfnid]).then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('TTFN.NOTIFICATION.ADD_SUCCESS');
        this.router.navigate([Pages.TTFN.path, version.ttfnid]).then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const selectedRecord = this.getSelectedRecord();
    if (selectedRecord.ttfnid) {
      this.timetableFieldNumberService
        .revokeTimetableFieldNumber(selectedRecord.ttfnid)
        .subscribe(() => {
          this.notificationService.success('TTFN.NOTIFICATION.REVOKE_SUCCESS');
          this.router
            .navigate([Pages.TTFN.path, selectedRecord.ttfnid])
            .then(() => this.ngOnInit());
        });
    }
  }

  deleteRecord(): void {
    const selectedRecord: TimetableFieldNumberVersion = this.getSelectedRecord();
    if (selectedRecord.ttfnid != null) {
      this.timetableFieldNumberService.deleteVersions(selectedRecord.ttfnid).subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
    }
  }

  getFormGroup(version: TimetableFieldNumberVersion): FormGroup {
    return new FormGroup<TimetableFieldNumberDetailFormGroup>(
      {
        swissTimetableFieldNumber: new FormControl(version.swissTimetableFieldNumber, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.sid4pt,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        ttfnid: new FormControl(version.ttfnid),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        number: new FormControl(version.number, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.numericWithDot,
        ]),
        description: new FormControl(version.description, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        comment: new FormControl(version.comment, [
          AtlasFieldLengthValidator.comments,
          AtlasCharsetsValidator.iso88591,
        ]),
        status: new FormControl(version.status),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  getPageType(): Page {
    return Pages.TTFN;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Ttfn;
  }
}
