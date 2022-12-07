import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  LineType,
  LineVersionSnapshot,
  PaymentType,
  Workflow,
  WorkflowService,
} from '../../../../api';
import { Router } from '@angular/router';
import { FormControl, FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import moment from 'moment/moment';
import { Pages } from '../../../pages';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { LineVersionSnapshotDetailFormGroup } from './line-version-snapshot-detail-form-group';
import { takeUntil } from 'rxjs/operators';

@Component({
  templateUrl: './line-version-snapshot-detail.component.html',
  styleUrls: ['./line-version-snapshot-detail.component.scss'],
})
export class LineVersionSnapshotDetailComponent implements OnInit, OnDestroy {
  TYPE_OPTIONS = Object.values(LineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);
  lineVersionSnapshot!: LineVersionSnapshot;
  workflow!: Workflow;
  form!: FormGroup;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private router: Router,
    private dialogRef: MatDialogRef<LineVersionSnapshotDetailComponent>,
    private workflowService: WorkflowService
  ) {}

  ngOnInit() {
    this.lineVersionSnapshot = this.readRecord();
    this.form = this.getFormGroup(this.lineVersionSnapshot);
    this.form.disable();
    this.workflowService
      .getWorkflow(this.lineVersionSnapshot.workflowId)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((workflow) => {
        this.workflow = workflow;
      });
  }

  backToOverview(): void {
    this.dialogRef.close();
  }

  readRecord(): LineVersionSnapshot {
    return this.dialogData.lineVersionSnapshot;
  }

  navigateToLineVersion() {
    this.router
      .navigate([Pages.LIDI.path, Pages.LINES.path, this.lineVersionSnapshot.slnid], {
        queryParams: { id: this.lineVersionSnapshot.parentObjectId },
      })
      .then();
  }

  getFormGroup(version: LineVersionSnapshot): FormGroup {
    return new FormGroup<LineVersionSnapshotDetailFormGroup>({
      parentObjectId: new FormControl(version.parentObjectId),
      workflowId: new FormControl(version.workflowId),
      workflowStatus: new FormControl(version.workflowStatus),
      swissLineNumber: new FormControl(version.description!),
      lineType: new FormControl(version.lineType),
      paymentType: new FormControl(version.paymentType),
      businessOrganisation: new FormControl(version.businessOrganisation),
      number: new FormControl(version.number),
      alternativeName: new FormControl(version.alternativeName),
      combinationName: new FormControl(version.combinationName),
      longName: new FormControl(version.longName),
      icon: new FormControl(version.icon),
      colorFontRgb: new FormControl(version.colorFontRgb),
      colorBackRgb: new FormControl(version.colorBackRgb),
      colorFontCmyk: new FormControl(version.colorFontCmyk),
      colorBackCmyk: new FormControl(version.colorBackCmyk),
      description: new FormControl(version.description),
      validFrom: new FormControl(version.validFrom ? moment(version.validFrom) : version.validFrom),
      validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo),
      comment: new FormControl(version.comment),
      etagVersion: new FormControl(version.etagVersion),
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
