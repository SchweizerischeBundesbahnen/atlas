import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DetailHelperService } from '../../../../../../core/detail/detail-helper.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { AtlasCharsetsValidator } from '../../../../../../core/validation/charsets/atlas-charsets-validator';
import { StopPointTerminationDialogData } from './stop-point-termination-dialog-data';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { NotificationService } from '../../../../../../core/notification/notification.service';
import { UserService } from '../../../../../../core/auth/user/user.service';
import { AtlasFieldLengthValidator } from 'src/app/core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../../../core/validation/whitespace/whitespace-validator';
import { StartTerminationStopPointAddWorkflow } from '../../../../../../api/model/startTerminationStopPointAddWorkflow';

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
    private readonly dialogRef: MatDialogRef<StopPointTerminationDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public readonly data: StopPointTerminationDialogData,
    private readonly detailHelperService: DetailHelperService,
    private readonly stopPointTerminationWorkflowService: StopPointTerminationWorkflowService,
    private readonly notificationService: NotificationService,
    private readonly userService: UserService
  ) {}

  form!: FormGroup<StartTerminationStopPointAddWorkflowFormGroup>;

  ngOnInit(): void {
    this.initForm();
  }

  startTermination() {
    const startTerminationValue = this.getStartTermination();
    if (this.form.valid) {
      this.form.disable();
      this.stopPointTerminationWorkflowService
        .startTermination(startTerminationValue)
        .subscribe(() => {
          this.notificationService.success('WORKFLOW.NOTIFICATION.ADD.SUCCESS');
          this.dialogRef.close(true);
        });
    }
  }

  private getStartTermination() {
    this.form.controls.sloid.setValue(this.data.sloid!);
    this.form.controls.versionId.setValue(this.data.versionId!);
    this.form.controls.boTerminationDate.setValue(this.data.boTerminationDate);
    this.form.controls.applicantMail.setValue(
      this.userService.currentUser!.email
    );
    return this.form.getRawValue() as unknown as StartTerminationStopPointAddWorkflow;
  }

  cancel() {
    this.detailHelperService
      .confirmLeaveDirtyForm(this.form)
      .subscribe((confirmed) => {
        if (confirmed) {
          this.dialogRef.close(false);
        }
      });
  }

  private initForm() {
    this.form = new FormGroup<StartTerminationStopPointAddWorkflowFormGroup>({
      versionId: new FormControl(undefined, [Validators.required]),
      sloid: new FormControl('', [Validators.required]),
      applicantMail: new FormControl('', [Validators.required]),
      workflowComment: new FormControl('', [
        AtlasFieldLengthValidator.comments,
        AtlasCharsetsValidator.iso88591,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
      ]),
      boTerminationDate: new FormControl(undefined, [Validators.required]),
    });
  }
}

export interface StartTerminationStopPointAddWorkflowFormGroup {
  versionId: FormControl<number | null | undefined>;
  sloid: FormControl<string | null | undefined>;
  workflowComment: FormControl<string | null | undefined>;
  boTerminationDate: FormControl<Date | null | undefined>;
  applicantMail: FormControl<string | null | undefined>;
}
