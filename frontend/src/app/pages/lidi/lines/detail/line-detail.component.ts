import {Component, OnInit} from '@angular/core';
import {
  ApplicationType,
  CreateLineVersionV2,
  LinesService,
  LineType,
  LineVersionV2,
  LineVersionWorkflow,
  Status,
  UpdateLineVersionV2,
} from '../../../../api';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {DialogService} from '../../../../core/components/dialog/dialog.service';
import moment from 'moment';
import {DateRangeValidator} from '../../../../core/validation/date-range/date-range-validator';
import {Pages} from '../../../pages';
import {Page} from 'src/app/core/model/page';
import {AtlasCharsetsValidator} from '../../../../core/validation/charsets/atlas-charsets-validator';
import {WhitespaceValidator} from '../../../../core/validation/whitespace/whitespace-validator';
import {AtlasFieldLengthValidator} from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import {LineDetailFormGroup} from './line-detail-form-group';
import {ValidityService} from "../../../sepodi/validity/validity.service";
import {PermissionService} from "../../../../core/auth/permission/permission.service";
import {BaseDetailController} from "../../../../core/components/base-detail/base-detail-controller";
import {catchError} from "rxjs";
import {NotificationService} from "../../../../core/notification/notification.service";

@Component({
  templateUrl: './line-detail.component.html',
  styleUrls: ['./line-detail.component.scss'],
  providers: [ValidityService],
})
export class LineDetailComponent extends BaseDetailController<LineVersionV2> implements OnInit {
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
    protected validityService: ValidityService,
  ) {
    super(router, dialogService, notificationService, permissionService, activatedRoute, validityService);
  }

  ngOnInit() {
    super.ngOnInit();
    this.isShowLineSnapshotHistory = this.showSnapshotHistoryLink();
    if (!this.isNewRecord()) {
      this.lineType = this.form.value.lineType
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
    const lineForm = this.form.value;//pass to update?
    const updateLine: UpdateLineVersionV2 = {
      creationDate: lineForm.creationDate,
      creator: lineForm.creator,
      editionDate: lineForm.editionDate,
      editor: lineForm.editor,
      swissLineNumber: lineForm.swissLineNumber,
      number: lineForm.number,
      longName: lineForm.longName,
      description: lineForm.description,
      validFrom: lineForm.validFrom,
      validTo: lineForm.validTo,
      businessOrganisation: lineForm.businessOrganisation,
      comment: lineForm.comment,
      etagVersion: lineForm.etagVersion,
      lineVersionWorkflows: lineForm.lineVersionWorkflows,
      lineConcessionType: lineForm.lineConcessionType,
      shortNumber: lineForm.shortNumber,
      offerCategory: lineForm.offerCategory
    }
    this.linesService
      .updateLineVersion(this.getId(), updateLine)
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

  conditionalValidation() {
    if (this.form.controls.lineType.value !== LineType.Orderly) {
      this.isLineConcessionTypeRequired = false;
      this.form.controls.lineConcessionType.clearValidators();
      this.form.controls.lineConcessionType.updateValueAndValidity();
    } else {
      this.isLineConcessionTypeRequired = true;
      this.form.controls.lineConcessionType.setValidators([Validators.required]);
      this.form.controls.lineConcessionType.updateValueAndValidity();
    }
    this.form.updateValueAndValidity();
  }

  createRecord(): void {
    const lineForm = this.form.value; //pass to create
    const createLineVersionV2: CreateLineVersionV2 = {
      lineType: lineForm.lineType,
      creationDate: lineForm.creationDate,
      creator: lineForm.creator,
      editionDate: lineForm.editionDate,
      editor: lineForm.editor,
      swissLineNumber: lineForm.swissLineNumber,
      number: lineForm.number,
      longName: lineForm.longName,
      description: lineForm.description,
      validFrom: lineForm.validFrom,
      validTo: lineForm.validTo,
      businessOrganisation: lineForm.businessOrganisation,
      comment: lineForm.comment,
      etagVersion: lineForm.etagVersion,
      lineVersionWorkflows: lineForm.lineVersionWorkflows,
      lineConcessionType: lineForm.lineConcessionType,
      shortNumber: lineForm.shortNumber,
      offerCategory: lineForm.offerCategory
    }
    this.form.disable();
    this.linesService
      .createLineVersionV2(createLineVersionV2)
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
        this.notificationService.success('LIDI.LINE.NOTIFICATION.REVOKE_SUCCESS');
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
        this.notificationService.success('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
    }
  }

  getFormGroup(version: LineVersionV2): FormGroup {
    let formGroup = new FormGroup<LineDetailFormGroup>(
      {
        swissLineNumber: new FormControl(version.swissLineNumber, [
          Validators.required,
          Validators.maxLength(50),
          AtlasCharsetsValidator.sid4pt,
        ]),
        lineType: new FormControl(version.lineType, [Validators.required]),
        offerCategory: new FormControl(version.offerCategory, [Validators.required]),
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
        shortNumber: new FormControl(version.shortNumber, [
          AtlasFieldLengthValidator.length_10,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        lineConcessionType: new FormControl(version.lineConcessionType, [Validators.required]),
        // lineConcessionType: new FormControl(version.lineConcessionType, version.lineType == "ORDERLY" ? [Validators.required] : []),
        longName: new FormControl(version.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
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
    return formGroup;
  }

  getFormControlsToDisable(): string[] {
    return this.record.status === Status.InReview ? ['validFrom', 'validTo', 'lineType'] : [];
  }
}
