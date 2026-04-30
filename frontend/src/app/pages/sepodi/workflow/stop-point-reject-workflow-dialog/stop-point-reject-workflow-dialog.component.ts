import { Component, inject, OnInit } from '@angular/core';
import {
  StopPointRejectWorkflowFormGroup,
  StopPointRejectWorkflowFormGroupBuilder,
} from './stop-point-reject-workflow-form-group';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RejectType, StopPointRejectWorkflowDialogData } from './stop-point-reject-workflow-dialog-data';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StopPointRejectWorkflow } from '../../../../api';
import { ValidationService } from '../../../../core/validation/validation.service';
import { Pages } from '../../../pages';
import { Router } from '@angular/router';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';

type DialogAction = Extract<RejectType, 'REJECT' | 'CANCEL'>;
const titleTranslationKeys: Record<DialogAction, string> = {
  REJECT: 'WORKFLOW.BUTTON.REJECT',
  CANCEL: 'WORKFLOW.BUTTON.CANCEL',
} as const;

@Component({
  selector: 'atlas-stop-point-reject-workflow-dialog',
  templateUrl: './stop-point-reject-workflow-dialog.component.html',
  styleUrl: './stop-point-reject-workflow-dialog.component.scss',
  imports: [
    DialogCloseComponent,
    DialogContentComponent,
    ReactiveFormsModule,
    TextFieldComponent,
    DialogFooterComponent,
    TranslatePipe,
    CommentComponent,
  ],
  providers: [TranslatePipe],
})
export class StopPointRejectWorkflowDialogComponent implements OnInit {
  formGroup!: FormGroup<StopPointRejectWorkflowFormGroup>;

  public readonly data: StopPointRejectWorkflowDialogData = inject(MAT_DIALOG_DATA);
  private readonly stopPointWorkflowService = inject(StopPointWorkflowService);
  private readonly dialogRef = inject(MatDialogRef<StopPointRejectWorkflowDialogComponent>);
  private readonly userAdministrationService = inject(UserAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly detailHelperService = inject(DetailDialogHelperService);
  private readonly router = inject(Router);

  protected readonly titleTranslationKey: string = (() => {
    if (this.data.rejectType === 'RESTART') {
      throw new Error('Restart is not supported in StopPointRejectWorkflowDialogComponent');
    }
    return titleTranslationKeys[this.data.rejectType];
  })();

  ngOnInit(): void {
    this.formGroup = StopPointRejectWorkflowFormGroupBuilder.initFormGroup();
    this.populateUserDataFormFromAuthenticatedUser();
  }

  private populateUserDataFormFromAuthenticatedUser() {
    this.formGroup.reset();
    this.userAdministrationService.getCurrentUser().subscribe((user) => {
      this.formGroup.controls.firstName.setValue(user.firstName);
      this.formGroup.controls.lastName.setValue(user.lastName);
      this.formGroup.controls.mail.setValue(user.mail!);
    });
  }

  closeDialog() {
    this.detailHelperService.confirmLeaveDirtyForm(this.formGroup).subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(true);
      }
    });
  }

  rejectWorkflow() {
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      const stopPointRejectWorkflow = StopPointRejectWorkflowFormGroupBuilder.buildStopPointRejectWorkflow(
        this.formGroup
      );
      this.formGroup.disable();
      if (this.data.rejectType === 'REJECT') {
        this.doReject(stopPointRejectWorkflow);
      }
      if (this.data.rejectType === 'CANCEL') {
        this.doCancel(stopPointRejectWorkflow);
      }
    }
  }

  private doCancel(stopPointRejectWorkflow: StopPointRejectWorkflow) {
    this.stopPointWorkflowService
      .cancelStopPointWorkflow(this.data.workflowId, stopPointRejectWorkflow)
      .subscribe(() => {
        this.notificationService.success('WORKFLOW.NOTIFICATION.CHECK.CANCELED');
        this.dialogRef.close();
        this.navigateToWorkflow();
      });
  }

  private doReject(stopPointRejectWorkflow: StopPointRejectWorkflow) {
    this.stopPointWorkflowService
      .rejectStopPointWorkflow(this.data.workflowId, stopPointRejectWorkflow)
      .subscribe(() => {
        this.notificationService.success('WORKFLOW.NOTIFICATION.CHECK.REJECTED');
        this.dialogRef.close();
        this.navigateToWorkflow();
      });
  }

  private navigateToWorkflow() {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate([Pages.SEPODI.path, Pages.WORKFLOWS.path, this.data.workflowId]).then(() => {});
    });
  }
}
