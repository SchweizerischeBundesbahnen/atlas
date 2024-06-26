import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators,} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatStepperModule} from '@angular/material/stepper';
import {TranslateModule} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogClose, MatDialogRef} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {FormModule} from '../../../../../core/module/form.module';
import {CoreModule} from '../../../../../core/module/core.module';
import {DialogService} from '../../../../../core/components/dialog/dialog.service';
import {JudgementType, ReadDecision, StopPointWorkflowService} from "../../../../../api";
import {DecisionDetailDialogData} from "./decision-detail-dialog.service";
import {AtlasFieldLengthValidator} from "../../../../../core/validation/field-lengths/atlas-field-length-validator";
import {DecisionOverrideComponent} from "./override/decision-override.component";

@Component({
  selector: 'decision-detail-dialog',
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
    DecisionOverrideComponent,
  ],
  templateUrl: './decision-detail-dialog.component.html',
  styleUrl: './decision-detail-dialog.component.scss',
})
export class DecisionDetailDialogComponent implements OnInit {

  @ViewChild(DecisionOverrideComponent) decisionOverrideComponent!:DecisionOverrideComponent;

  existingDecision!: ReadDecision;
  decisionForm = new FormGroup(
    {
      firstName: new FormControl('', [Validators.required]),
      lastName: new FormControl('', [Validators.required]),
      organisation: new FormControl('', [Validators.required]),
      function: new FormControl('', [Validators.required]),
      decision: new FormControl<boolean|null|undefined>(null, [Validators.required]),
      comment: new FormControl<string|null|undefined>('', [AtlasFieldLengthValidator.comments]),
    },
  );

  constructor(
    private dialogService: DialogService,
    private dialogRef: MatDialogRef<DecisionDetailDialogComponent>,
    private stopPointWorkflowService: StopPointWorkflowService,
    @Inject(MAT_DIALOG_DATA) protected decisionDetailDialogData: DecisionDetailDialogData,
  ) {}

  ngOnInit() {
    this.decisionForm.patchValue(this.decisionDetailDialogData.examinant.value);
    if (this.decisionDetailDialogData.examinant.controls.judgement.value) {
      this.stopPointWorkflowService.getDecision(this.decisionDetailDialogData.examinant.controls.id.value!).subscribe(decision => {
        this.existingDecision = decision;
        this.decisionForm.controls.decision.setValue(decision.judgement === JudgementType.Yes);
        this.decisionForm.controls.comment.setValue(decision.motivation);
      });
    }
    this.decisionForm.disable();
  }

  close() {
    this.dialogRef.close();
  }

  overrideDecision() {
    this.decisionOverrideComponent.saveOverride();
  }
}
