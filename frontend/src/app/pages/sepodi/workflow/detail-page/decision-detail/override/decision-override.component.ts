import {Component, Input, OnInit} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule,} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatStepperModule} from '@angular/material/stepper';
import {TranslateModule} from '@ngx-translate/core';
import {MatDialogClose} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {FormModule} from "../../../../../../core/module/form.module";
import {CoreModule} from "../../../../../../core/module/core.module";
import {ApplicationType, Decision, JudgementType, ReadDecision, StopPointWorkflowService} from "../../../../../../api";
import {PermissionService} from "../../../../../../core/auth/permission/permission.service";
import {DecisionOverrideFormGroup, DecisionOverrideFormGroupBuilder} from "./decision-override-form-group";

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
export class DecisionOverrideComponent implements OnInit {

  protected readonly JudgementType = JudgementType;

  @Input() workflowId!: number;
  @Input() examinantId!: number;
  @Input() existingDecision?: ReadDecision;

  isSepodiSupervisor = false;
  formGroup!:FormGroup<DecisionOverrideFormGroup>;

  constructor(
    private stopPointWorkflowService: StopPointWorkflowService,
    private permissionService: PermissionService
  ) {}

  ngOnInit() {
    this.formGroup = DecisionOverrideFormGroupBuilder.buildFormGroup(this.existingDecision);
    this.isSepodiSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Sepodi);
    if (!this.isSepodiSupervisor) {
      this.formGroup.disable();
    }
  }

  saveOverride(){
    this.stopPointWorkflowService.overrideVoteWorkflow(this.workflowId, this.examinantId, {} as Decision).subscribe(() => {
// success
    });
  }

}
