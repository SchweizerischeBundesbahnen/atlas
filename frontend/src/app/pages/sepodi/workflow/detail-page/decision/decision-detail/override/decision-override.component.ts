import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatStepperModule } from '@angular/material/stepper';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import {
  DecisionOverrideFormGroup,
  DecisionOverrideFormGroupBuilder,
} from './decision-override-form-group';
import { Router } from '@angular/router';
import { DecisionDetailDialogComponent } from '../decision-detail-dialog.component';
import { FormModule } from 'src/app/core/module/form.module';
import { CoreModule } from 'src/app/core/module/core.module';
import {
  JudgementType,
  StopPointWorkflowService,
  ReadDecision,
  OverrideDecision,
  ApplicationType,
} from 'src/app/api';
import { NotificationService } from 'src/app/core/notification/notification.service';
import { PermissionService } from 'src/app/core/auth/permission/permission.service';
import { Pages } from 'src/app/pages/pages';
import {ValidationService} from "../../../../../../../core/validation/validation.service";

@Component({
  selector: 'decision-override',
  standalone: true,
  imports: [
    MatButtonModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    TranslateModule,
    MatDialogClose,
    MatIconModule,
    FormModule,
    CoreModule,
  ],
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
