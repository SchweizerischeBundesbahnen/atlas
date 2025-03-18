import {Component, OnInit} from '@angular/core';
import {LinesService, LineVersion, LineVersionSnapshot, Workflow, WorkflowService,} from '../../../../api';
import {ActivatedRoute, Router} from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import moment from 'moment';
import {Pages} from '../../../pages';
import {LineVersionSnapshotDetailFormGroup} from './line-version-snapshot-detail-form-group';
import {WorkflowFormGroup} from '../../../../core/workflow/workflow-form-group';
import {WorkflowCheckFormGroup} from '../../../../core/workflow/workflow-check-form/workflow-check-form-group';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { WorkflowFormComponent } from '../../../../core/workflow/workflow-form/workflow-form.component';
import { NgIf } from '@angular/common';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { LineDetailFormComponent } from '../../lines/detail/line-detail-form/line-detail-form.component';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { BackButtonDirective } from '../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    templateUrl: './line-version-snapshot-detail.component.html',
    styleUrls: ['./line-version-snapshot-detail.component.scss'],
    imports: [ScrollToTopDirective, DetailPageContainerComponent, DetailPageContentComponent, DateRangeTextComponent, WorkflowFormComponent, ReactiveFormsModule, NgIf, LinkIconComponent, LineDetailFormComponent, UserDetailInfoComponent, DetailFooterComponent, AtlasButtonComponent, BackButtonDirective, TranslatePipe]
})
export class LineVersionSnapshotDetailComponent implements OnInit {
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
      lineConcessionType: new FormControl(version.lineConcessionType),
      offerCategory: new FormControl(version.offerCategory),
      shortNumber: new FormControl(version.shortNumber),
      parentObjectId: new FormControl(version.parentObjectId),
      workflowId: new FormControl(version.workflowId),
      workflowStatus: new FormControl(version.workflowStatus),
      swissLineNumber: new FormControl(version.description!),
      lineType: new FormControl(version.lineType),
      businessOrganisation: new FormControl(version.businessOrganisation),
      number: new FormControl(version.number),
      longName: new FormControl(version.longName),
      description: new FormControl(version.description),
      validFrom: new FormControl(version.validFrom ? moment(version.validFrom) : version.validFrom),
      validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo),
      comment: new FormControl(version.comment),
      etagVersion: new FormControl(version.etagVersion),
      creationDate: new FormControl(version.creationDate),
      editionDate: new FormControl(version.editionDate),
      editor: new FormControl(version.editor),
      creator: new FormControl(version.creator)
    });
  }

  private checkLineVersionSNapshottedAlreadyExists() {
    this.lineService.getLineVersions(this.lineVersionSnapshot!.slnid!).subscribe({
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
    this.workflowService.getWorkflow(this.lineVersionSnapshot.workflowId).subscribe((workflow) => {
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
