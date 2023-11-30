import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  ApplicationType,
  LinesService,
  LineType,
  LineVersion,
  LineVersionWorkflow,
  Status,
} from '../../../../api';
import { BaseDetailController } from '../../../../core/components/base-detail/base-detail-controller';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { takeUntil } from 'rxjs/operators';
import { catchError, Subject } from 'rxjs';
import moment from 'moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { Pages } from '../../../pages';
import { Page } from 'src/app/core/model/page';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { LineDetailFormGroup } from './line-detail-form-group';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss'],
})
export class LineDetailComponent
  extends BaseDetailController<LineVersion>
  implements OnInit, OnDestroy
{
  isShowLineSnapshotHistory = false;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    protected router: Router,
    private linesService: LinesService,
    protected notificationService: NotificationService,
    protected dialogService: DialogService,
    protected authService: AuthService,
    protected activatedRoute: ActivatedRoute,
  ) {
    super(router, dialogService, notificationService, authService, activatedRoute);
  }

  ngOnInit() {
    super.ngOnInit();
    this.isShowLineSnapshotHistory = this.showSnapshotHistoryLink();
  }

  getPageType(): Page {
    return Pages.LINES;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Lidi;
  }

  readRecord(): LineVersion {
    return this.activatedRoute.snapshot.data.lineDetail;
  }

  getDetailHeading(record: LineVersion): string {
    return `${record.number ?? ''} - ${record.description ?? ''}`;
  }

  getDetailSubheading(record: LineVersion): string {
    return record.slnid!;
  }

  getDescriptionForWorkflow(): string {
    if (this.record) {
      if (this.record.description) {
        return this.record.description;
      }
    }
    return '';
  }

  navigateToSnapshot() {
    this.router
      .navigate([Pages.LIDI.path, Pages.WORKFLOWS.path], {
        queryParams: {
          slnid: this.record.slnid,
        },
      })
      .then();
  }

  isWorkflowable(): boolean {
    if (this.getPageType() === Pages.LINES) {
      if (this.record.status === Status.Draft || this.record.status === Status.InReview) {
        if (this.record.lineType === LineType.Orderly) return true;
      }
    }
    return false;
  }

  showSnapshotHistoryLink(): boolean {
    const lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.record.lineVersionWorkflows?.forEach((lvw) => lineVersionWorkflows.push(lvw));
    return (
      lineVersionWorkflows.length > 0 ||
      (this.record.lineType === LineType.Orderly && this.record.status === Status.Validated)
    );
  }

  updateRecord(): void {
    this.linesService
      .updateLineVersion(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, this.record.slnid])
          .then(() => this.ngOnInit());
      });
  }

  reloadRecord() {
    this.router
      .navigate([Pages.LIDI.path, Pages.LINES.path, this.record.slnid])
      .then(() => this.ngOnInit());
  }

  createRecord(): void {
    this.linesService
      .createLineVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const selectedLineVersion: LineVersion = this.getSelectedRecord();
    if (selectedLineVersion.slnid) {
      this.linesService.revokeLine(selectedLineVersion.slnid).subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.REVOKE_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, this.record.slnid])
          .then(() => this.ngOnInit());
      });
    }
  }

  deleteRecord(): void {
    const selectedLineVersion: LineVersion = this.getSelectedRecord();
    if (selectedLineVersion.slnid != null) {
      this.linesService.deleteLines(selectedLineVersion.slnid).subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
    }
  }

  getFormGroup(version: LineVersion): FormGroup {
    return new FormGroup<LineDetailFormGroup>(
      {
        swissLineNumber: new FormControl(version.swissLineNumber, [
          Validators.required,
          Validators.maxLength(50),
          AtlasCharsetsValidator.sid4pt,
        ]),
        lineType: new FormControl(version.lineType, [Validators.required]),
        paymentType: new FormControl(version.paymentType, [Validators.required]),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
        ]),
        number: new FormControl(version.number, [
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        alternativeName: new FormControl(version.alternativeName, [
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        combinationName: new FormControl(version.combinationName, [
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        longName: new FormControl(version.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        icon: new FormControl(version.icon, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        colorFontRgb: new FormControl(version.colorFontRgb || '#000000', [Validators.required]),
        colorBackRgb: new FormControl(version.colorBackRgb || '#FFFFFF', [Validators.required]),
        colorFontCmyk: new FormControl(version.colorFontCmyk || '100,100,100,100', [
          Validators.required,
        ]),
        colorBackCmyk: new FormControl(version.colorBackCmyk || '0,0,0,0', [Validators.required]),
        description: new FormControl(version.description, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        comment: new FormControl(version.comment, [
          AtlasFieldLengthValidator.comments,
          AtlasCharsetsValidator.iso88591,
        ]),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  getFormControlsToDisable(): string[] {
    return this.record.status === Status.InReview ? ['validFrom', 'validTo', 'lineType'] : [];
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }
}
