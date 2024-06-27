import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule,} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatStepperModule} from '@angular/material/stepper';
import {TranslateModule} from '@ngx-translate/core';
import {MatDialogClose, MatDialogRef} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {FormModule} from "../../../../../../core/module/form.module";
import {CoreModule} from "../../../../../../core/module/core.module";
import {ApplicationType, JudgementType, OverrideDecision, ReadDecision, StopPointWorkflowService} from "../../../../../../api";
import {PermissionService} from "../../../../../../core/auth/permission/permission.service";
import {DecisionOverrideFormGroup, DecisionOverrideFormGroupBuilder} from "./decision-override-form-group";
import {Pages} from "../../../../../pages";
import {NotificationService} from "../../../../../../core/notification/notification.service";
import {Router} from "@angular/router";
import {DecisionDetailDialogComponent} from "../decision-detail-dialog.component";

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
  ) {
  }

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
    if (this.formGroup.valid) {
      const overrideDecision: OverrideDecision = this.formGroup.value as OverrideDecision;
      overrideDecision.fotMotivation = overrideDecision.fotMotivation?.length === 0 ? undefined : overrideDecision.fotMotivation;
      this.stopPointWorkflowService.overrideVoteWorkflow(this.workflowId, this.examinantId, overrideDecision).subscribe(() => {
        this.notificationService.success('WORKFLOW.NOTIFICATION.VOTE.SUCCESS');
        this.matDialogRef.close();
        this.router.navigateByUrl('/').then(() => {
          this.router.navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, this.workflowId]).then(() => {
          });
        });
      });
    }
  }

}
