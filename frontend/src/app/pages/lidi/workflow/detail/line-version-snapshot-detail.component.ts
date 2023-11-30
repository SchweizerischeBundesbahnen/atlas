import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  LinesService,
  LineVersion,
  LineVersionSnapshot,
  Workflow,
  WorkflowService,
} from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import moment from 'moment';
import { Pages } from '../../../pages';
import { LineVersionSnapshotDetailFormGroup } from './line-version-snapshot-detail-form-group';
import { takeUntil } from 'rxjs/operators';
import { WorkflowFormGroup } from '../../../../core/workflow/workflow-form-group';
import { WorkflowCheckFormGroup } from '../../../../core/workflow/workflow-check-form/workflow-check-form-group';

@Component({
  templateUrl: './line-version-snapshot-detail.component.html',
  styleUrls: ['./line-version-snapshot-detail.component.scss'],
})
export class LineVersionSnapshotDetailComponent implements OnInit, OnDestroy {
  lineVersionSnapshot!: LineVersionSnapshot;
  showWorkflowCheckForm = false;
  versionAlreadyExists = true;
  workflow!: Workflow;
  lineVersionSnapshotForm!: FormGroup<LineVersionSnapshotDetailFormGroup>;
  workflowStartedFormGroup: FormGroup<WorkflowFormGroup> = new FormGroup<WorkflowFormGroup>({
    comment: new FormControl(''),
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    function: new FormControl(''),
    mail: new FormControl(''),
  });

  workflowCheckFormGroup: FormGroup<WorkflowCheckFormGroup> = new FormGroup<WorkflowCheckFormGroup>(
    {
      comment: new FormControl(''),
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      function: new FormControl(''),
    },
  );
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private workflowService: WorkflowService,
    private lineService: LinesService,
  ) {}

  ngOnInit() {
    this.lineVersionSnapshot = this.readRecord();
    this.initLineVersionSnapshotForm();
    this.initWorkflowForms();
    this.checkLineVersionSNapshottedAlreadyExists();
  }

  readRecord(): LineVersionSnapshot {
    return this.activatedRoute.snapshot.data.lineVersionSnapshot;
  }

  navigateToLine() {
    if (this.lineVersionSnapshot.slnid) {
      const urlCommands = [Pages.LIDI.path, Pages.LINES.path, this.lineVersionSnapshot.slnid];
      if (this.versionAlreadyExists) {
        this.navigateToVersionById(urlCommands);
      } else {
        this.novigateToLineBySlnid(urlCommands);
      }
    }
  }

  populateLineVersionSnapshotFormGroup(
    version: LineVersionSnapshot,
  ): FormGroup<LineVersionSnapshotDetailFormGroup> {
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
      creationDate: new FormControl(version.creationDate),
      editionDate: new FormControl(version.editionDate),
      editor: new FormControl(version.editor),
      creator: new FormControl(version.creator),
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.unsubscribe();
  }

  private checkLineVersionSNapshottedAlreadyExists() {
    this.lineService
      .getLineVersions(this.lineVersionSnapshot!.slnid!)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe({
        next: (lineVersions) => {
          const lineVersionsFiltered: LineVersion[] = lineVersions.filter(
            (version) => version.id === this.lineVersionSnapshot.parentObjectId,
          );
          if (lineVersionsFiltered.length === 0) {
            this.versionAlreadyExists = false;
          }
        },
        error: () => {
          this.versionAlreadyExists = false;
        },
      });
  }

  private initWorkflowForms() {
    this.workflowService
      .getWorkflow(this.lineVersionSnapshot.workflowId)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((workflow) => {
        this.workflow = workflow;
        this.pupulateWorkflowStartedFormGroup();
        if (
          this.lineVersionSnapshot.workflowStatus === 'APPROVED' ||
          this.lineVersionSnapshot.workflowStatus === 'REJECTED'
        ) {
          this.showWorkflowCheckForm = true;
          this.populeteWorkflowCheckFormGroup();
        }
      });
  }

  private initLineVersionSnapshotForm() {
    this.lineVersionSnapshotForm = this.populateLineVersionSnapshotFormGroup(
      this.lineVersionSnapshot,
    );
    this.lineVersionSnapshotForm.disable();
  }

  private novigateToLineBySlnid(urlCommands: string[]) {
    this.router.navigate(urlCommands).then();
  }

  private navigateToVersionById(urlCommands: string[]) {
    this.router
      .navigate(urlCommands, {
        queryParams: { id: this.lineVersionSnapshot.parentObjectId },
      })
      .then();
  }

  private populeteWorkflowCheckFormGroup() {
    this.workflowCheckFormGroup.controls.firstName.setValue(this.workflow.examinant?.firstName);
    this.workflowCheckFormGroup.controls.lastName.setValue(this.workflow.examinant?.lastName);
    this.workflowCheckFormGroup.controls.function.setValue(this.workflow.examinant?.personFunction);
    this.workflowCheckFormGroup.controls.comment.setValue(this.workflow.checkComment);
    this.workflowCheckFormGroup.disable();
  }

  private pupulateWorkflowStartedFormGroup() {
    this.workflowStartedFormGroup.controls.firstName.setValue(this.workflow.client?.firstName);
    this.workflowStartedFormGroup.controls.lastName.setValue(this.workflow.client?.lastName);
    this.workflowStartedFormGroup.controls.mail.setValue(this.workflow.client?.mail);
    this.workflowStartedFormGroup.controls.function.setValue(this.workflow.client?.personFunction);
    this.workflowStartedFormGroup.controls.comment.setValue(this.workflow.workflowComment);
    this.workflowStartedFormGroup.disable();
  }
}
