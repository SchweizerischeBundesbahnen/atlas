import {Component, Inject, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule,} from '@angular/forms';
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
import {StopPointWorkflowService} from "../../../../../api";
import {DecisionDetailDialogData} from "./decision-detail-dialog.service";

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
  ],
  templateUrl: './decision-detail-dialog.component.html',
  styleUrl: './decision-detail-dialog.component.scss',
})
export class DecisionDetailDialogComponent implements OnInit {

  constructor(
    private dialogService: DialogService,
    private dialogRef: MatDialogRef<DecisionDetailDialogComponent>,
    private stopPointWorkflowService: StopPointWorkflowService,
    @Inject(MAT_DIALOG_DATA) private decisionDetailDialogData: DecisionDetailDialogData,
  ) {}

  ngOnInit() {
    this.stopPointWorkflowService.getDecision(this.decisionDetailDialogData.examinantId).subscribe(decision => console.log(decision));
  }

  close() {
    this.dialogRef.close();
  }
}
