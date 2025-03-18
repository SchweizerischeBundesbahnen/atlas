import {Component, Input, OnChanges, OnInit} from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {DecisionOverrideFormGroup, DecisionOverrideFormGroupBuilder,} from './decision-override-form-group';
import {Router} from '@angular/router';
import {DecisionDetailDialogComponent} from '../decision-detail-dialog.component';
import {ApplicationType, JudgementType, OverrideDecision, ReadDecision, StopPointWorkflowService,} from 'src/app/api';
import {NotificationService} from 'src/app/core/notification/notification.service';
import {PermissionService} from 'src/app/core/auth/permission/permission.service';
import {Pages} from 'src/app/pages/pages';
import {ValidationService} from "../../../../../../../core/validation/validation.service";
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { MatRadioGroup, MatRadioButton } from '@angular/material/radio';
import { AtlasFieldErrorComponent } from '../../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'decision-override',
    templateUrl: './decision-override.component.html',
    imports: [ReactiveFormsModule, TextFieldComponent, MatRadioGroup, MatRadioButton, AtlasFieldErrorComponent, CommentComponent, TranslatePipe]
})
export class DecisionOverrideComponent implements OnInit, OnChanges {
  protected readonly JudgementType = JudgementType;

  @Input() workflowId!: number;
  @Input() examinantId!: number;
  @Input() existingDecision?: ReadDecision;
  @Input() enabled= true;

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
    this.formGroup.disable();
    if (this.enabled && this.isSepodiSupervisor) {
      this.formGroup.enable();
    }
  }

  saveOverride() {
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      const overrideDecision: OverrideDecision = this.formGroup.value as OverrideDecision;
      overrideDecision.fotMotivation =
        overrideDecision.fotMotivation?.length === 0 ? undefined : overrideDecision.fotMotivation;
      this.formGroup.disable();
      this.stopPointWorkflowService
        .overrideVoteWorkflow(this.workflowId, this.examinantId, overrideDecision)
        .subscribe(() => {
          this.notificationService.success('WORKFLOW.NOTIFICATION.VOTE.SUCCESS');
          this.matDialogRef.close();
          this.router.navigateByUrl('/').then(() => {
            this.router
              .navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, this.workflowId])
              .then(() => {
              });
          });
        });
    }
  }
}
