import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {DecisionOverrideFormGroup, DecisionOverrideFormGroupBuilder,} from './decision-override-form-group';
import {Router} from '@angular/router';
import {DecisionDetailDialogComponent} from '../decision-detail-dialog.component';
import {ApplicationType, JudgementType, OverrideDecision, ReadDecision, StopPointWorkflowService,} from 'src/app/api';
import {NotificationService} from 'src/app/core/notification/notification.service';
import {PermissionService} from 'src/app/core/auth/permission/permission.service';
import {Pages} from 'src/app/pages/pages';
import {ValidationService} from "../../../../../../../core/validation/validation.service";

@Component({
  selector: 'decision-override',
  templateUrl: './decision-override.component.html',
})
export class DecisionOverrideComponent implements OnInit, OnChanges {
  protected readonly JudgementType = JudgementType;

  @Input() workflowId!: number;
  @Input() examinantId!: number;
  @Input() existingDecision?: ReadDecision;

  isSepodiSupervisor = false;
  formGroup!: FormGroup<DecisionOverrideFormGroup>;

  constructor(
    private stopPointWorkflowService: StopPointWorkflowService,
    private permissionService: PermissionService,
    private notificationService: NotificationService,
    private matDialogRef: MatDialogRef<DecisionDetailDialogComponent>,
    private router: Router,
  ) {}

  ngOnInit() {
    this.init();
  }

  ngOnChanges() {
    // To load async loaded existingDecision
    this.init();
  }

  private init() {
    this.formGroup = DecisionOverrideFormGroupBuilder.buildFormGroup(this.existingDecision);
    this.isSepodiSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Sepodi);
    if (!this.isSepodiSupervisor) {
      this.formGroup.disable();
    }
  }

  saveOverride() {
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      const overrideDecision: OverrideDecision = this.formGroup.value as OverrideDecision;
      overrideDecision.fotMotivation =
        overrideDecision.fotMotivation?.length === 0 ? undefined : overrideDecision.fotMotivation;
      this.stopPointWorkflowService
        .overrideVoteWorkflow(this.workflowId, this.examinantId, overrideDecision)
        .subscribe(() => {
          this.notificationService.success('WORKFLOW.NOTIFICATION.VOTE.SUCCESS');
          this.matDialogRef.close();
          this.router.navigateByUrl('/').then(() => {
            this.router
              .navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, this.workflowId])
              .then(() => {});
          });
        });
    }
  }
}
