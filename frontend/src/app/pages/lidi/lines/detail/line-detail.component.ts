import { Component, OnInit } from '@angular/core';
import {
  ApplicationType,
  LinesService,
  LineType,
  LineVersionV2,
  LineVersionWorkflow,
  Status,
} from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import moment from 'moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { Pages } from '../../../pages';
import { Page } from 'src/app/core/model/page';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { LineDetailFormGroup } from './line-detail-form-group';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { BaseDetailController } from '../../../../core/components/base-detail/base-detail-controller';
import { catchError } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';

@Component({
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss'],
  providers: [ValidityService],
})
export class LineDetailComponent
  extends BaseDetailController<LineVersionV2>
  implements OnInit
{
  isShowLineSnapshotHistory = false;

  _lineType!: LineType;
  get lineType(): LineType {
    return this._lineType;
  }

  set lineType(lineType: LineType) {
    this._lineType = lineType;
  }

  _isLineConcessionTypeRequired = true;
  get isLineConcessionTypeRequired(): boolean {
    return this._isLineConcessionTypeRequired;
  }

  set isLineConcessionTypeRequired(isRequired) {
    this._isLineConcessionTypeRequired = isRequired;
  }

  constructor(
    protected router: Router,
    private linesService: LinesService,
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
    this.isShowLineSnapshotHistory = this.showSnapshotHistoryLink();
    if (!this.isNewRecord()) {
      this.lineType = this.form.value.lineType;
      if (this.form.controls.lineType.value !== LineType.Orderly) {
        this.isLineConcessionTypeRequired = false;
      }
    }
  }

  getPageType(): Page {
    return Pages.LINES;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Lidi;
  }

  readRecord(): LineVersionV2 {
    return this.activatedRoute.snapshot.data.lineDetail;
  }

  getDetailHeading(record: LineVersionV2): string {
    return `${record.number ?? ''} - ${record.description ?? ''}`;
  }

  getDetailSubheading(record: LineVersionV2): string {
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
      if (
        this.record.status === Status.Draft ||
        this.record.status === Status.InReview
      ) {
        if (this.record.lineType === LineType.Orderly) return true;
      }
    }
    return false;
  }

  showSnapshotHistoryLink(): boolean {
    const lineVersionWorkflows: LineVersionWorkflow[] = [];
    this.record.lineVersionWorkflows?.forEach((lvw) =>
      lineVersionWorkflows.push(lvw)
    );
    return (
      lineVersionWorkflows.length > 0 ||
      (this.record.lineType === LineType.Orderly &&
        this.record.status === Status.Validated)
    );
  }

  updateRecord(): void {
    this.linesService
      .updateLineVersion(this.getId(), this.form.value)
      .pipe(catchError(this.handleError))
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

  isEditButtonVisible() {
    return (
      this.record.status !== 'IN_REVIEW' ||
      this.permissionService.isAtLeastSupervisor(ApplicationType.Lidi)
    );
  }

  conditionalValidation() {
    if (this.form.controls.lineType.value !== LineType.Orderly) {
      this.isLineConcessionTypeRequired = false;
      this.form.controls.lineConcessionType.clearValidators();
      this.form.controls.lineConcessionType.updateValueAndValidity();
      this.form.controls.swissLineNumber.clearValidators();
      this.form.controls.swissLineNumber.updateValueAndValidity();
    } else {
      this.isLineConcessionTypeRequired = true;
      this.form.controls.lineConcessionType.setValidators([
        Validators.required,
      ]);
      this.form.controls.lineConcessionType.updateValueAndValidity();
      this.form.controls.swissLineNumber.setValidators([Validators.required]);
      this.form.controls.swissLineNumber.updateValueAndValidity();
    }
    this.form.updateValueAndValidity();
  }

  subscribeToConditionalValidation() {
    this.form.controls.lineType.valueChanges.subscribe(() => {
      this.conditionalValidation();
    });
  }

  createRecord(): void {
    const lineForm = this.form.value; //pass to create
    this.form.disable();
    this.linesService
      .createLineVersionV2(lineForm)
      .pipe(catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const selectedLineVersion: LineVersionV2 = this.getSelectedRecord();
    if (selectedLineVersion.slnid) {
      this.linesService.revokeLine(selectedLineVersion.slnid).subscribe(() => {
        this.notificationService.success(
          'LIDI.LINE.NOTIFICATION.REVOKE_SUCCESS'
        );
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, this.record.slnid])
          .then(() => this.ngOnInit());
      });
    }
  }

  deleteRecord(): void {
    const selectedLineVersion: LineVersionV2 = this.getSelectedRecord();
    if (selectedLineVersion.slnid != null) {
      this.linesService.deleteLines(selectedLineVersion.slnid).subscribe(() => {
        this.notificationService.success(
          'LIDI.LINE.NOTIFICATION.DELETE_SUCCESS'
        );
        this.backToOverview();
      });
    }
  }

  getFormGroup(version: LineVersionV2): FormGroup {
    return new FormGroup<LineDetailFormGroup>(
      {
        swissLineNumber: new FormControl(version.swissLineNumber, [
          Validators.required,
          Validators.maxLength(50),
          AtlasCharsetsValidator.sid4pt,
        ]),
        lineType: new FormControl(version.lineType, [Validators.required]),
        offerCategory: new FormControl(version.offerCategory, [
          Validators.required,
        ]),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
        ]),
        number: new FormControl(version.number, [
          Validators.maxLength(8),
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        shortNumber: new FormControl(version.shortNumber, [
          Validators.maxLength(8),
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        lineConcessionType: new FormControl(version.lineConcessionType, [
          Validators.required,
        ]),
        longName: new FormControl(version.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        description: new FormControl(version.description, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
          Validators.required,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required]
        ),
        validTo: new FormControl(
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required]
        ),
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
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  getFormControlsToDisable(): string[] {
    return this.record.status === Status.InReview
      ? ['validFrom', 'validTo', 'lineType']
      : [];
  }
}
