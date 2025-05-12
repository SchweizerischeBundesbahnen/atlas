import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DetailHelperService } from '../../../../../core/detail/detail-helper.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DialogCloseComponent } from '../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { CommentComponent } from '../../../../../core/form-components/comment/comment.component';
import { AtlasCharsetsValidator } from '../../../../../core/validation/charsets/atlas-charsets-validator';
import { StopPointTerminationDialogData } from './stop-point-termination-dialog-data';

@Component({
  selector: 'app-stop-point-termination-dialog',
  templateUrl: './stop-point-termination-dialog.component.html',
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    DialogFooterComponent,
    TranslatePipe,
    CommentComponent,
  ],
})
export class StopPointTerminationDialogComponent implements OnInit {
  constructor(
    private dialogRef: MatDialogRef<StopPointTerminationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StopPointTerminationDialogData,
    private detailHelperService: DetailHelperService
  ) {}

  form!: FormGroup<StartTerminationStopPointAddWorkflowFormGroup>;
  ngOnInit(): void {
    this.form = new FormGroup<StartTerminationStopPointAddWorkflowFormGroup>({
      versionId: new FormControl(undefined, [Validators.required]),
      sloid: new FormControl('', [Validators.required]),
      workflowComment: new FormControl('', [
        Validators.required,
        Validators.maxLength(1500),
        AtlasCharsetsValidator.iso88591,
      ]),
      boTerminationDate: new FormControl(undefined, [Validators.required]),
    });
  }

  startTermination() {
    //TODO: validate form
    this.form.controls.sloid.setValue(this.data.sloid);
    this.form.controls.versionId.setValue(this.data.versionId);
    this.form.controls.boTerminationDate.setValue(this.data.boTerminationDate);
  }

  cancel() {
    this.detailHelperService
      .confirmLeaveDirtyForm(this.form)
      .subscribe((confirmed) => {
        if (confirmed) {
          this.dialogRef.close(true);
        }
      });
  }
}

export interface StartTerminationStopPointAddWorkflowFormGroup {
  versionId: FormControl<number | null | undefined>;
  sloid: FormControl<string | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  boTerminationDate: FormControl<Date | null | undefined>;
}
