import { Component, inject, OnInit, viewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { MatStep, MatStepper, MatStepperIcon } from '@angular/material/stepper';
import { TimetableHearingStatementV2 } from '../../../../../../api';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import {
  StatementDataProtectionFormGroup,
  StatementDataProtectionFormGroupBuilder,
  StatementDocumentDataProtectionFormGroup,
} from './statement-data-protection-check-form-group';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { NgTemplateOutlet } from '@angular/common';
import { FileComponent } from '../../../../../../core/components/file-upload/file/file.component';
import { FileDownloadService } from '../../../../../../core/components/file-upload/file/file-download.service';
import { TimetableHearingStatementInternalService } from '../../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { ValidationService } from '../../../../../../core/validation/validation.service';
import { NotificationService } from '../../../../../../core/notification/notification.service';

@Component({
  selector: 'atlas-statement-data-protection-check-dialog',
  templateUrl: './statement-data-protection-check.dialog.component.html',
  styleUrls: ['statement-data-protection-check.dialog.component.scss'],
  imports: [
    CommentComponent,
    ReactiveFormsModule,
    TranslatePipe,
    DialogCloseComponent,
    DialogContentComponent,
    MatStep,
    MatStepper,
    MatStepperIcon,
    MatRadioButton,
    MatRadioGroup,
    AtlasButtonComponent,
    FileComponent,
    AtlasFieldErrorComponent,
    NgTemplateOutlet,
  ],
  providers: [TranslatePipe],
})
export class StatementDataProtectionCheckDialogComponent implements OnInit {
  readonly statement = inject<TimetableHearingStatementV2>(MAT_DIALOG_DATA);

  private readonly dialogRef = inject(
    MatDialogRef<StatementDataProtectionCheckDialogComponent, boolean>
  );
  private readonly timetableHearingStatementsService = inject(
    TimetableHearingStatementInternalService
  );
  private readonly notificationService = inject(NotificationService);

  readonly stepper = viewChild.required<MatStepper>('stepper');

  statementFormGroup!: FormGroup<StatementDataProtectionFormGroup>;
  documentFormGroup!: FormGroup<StatementDocumentDataProtectionFormGroup>;
  hasDocuments = false;

  ngOnInit() {
    this.hasDocuments = (this.statement.documents?.length ?? 0) > 0;

    this.statementFormGroup =
      StatementDataProtectionFormGroupBuilder.buildStatementGroup(
        this.statement
      );
    this.documentFormGroup =
      StatementDataProtectionFormGroupBuilder.buildDocumentGroup(
        this.statement
      );
  }

  completeTextDataProtection() {
    ValidationService.validateForm(this.statementFormGroup);
    if (this.statementFormGroup.valid) {
      if (this.hasDocuments) {
        this.stepper().next();
      } else {
        this.completeDataProtection();
      }
    }
  }

  completeFileDataProtection() {
    ValidationService.validateForm(this.documentFormGroup);
    if (this.documentFormGroup.valid) {
      this.completeDataProtection();
    }
  }

  private completeDataProtection() {
    this.timetableHearingStatementsService
      .checkDataProtection(
        StatementDataProtectionFormGroupBuilder.toModel(
          this.documentFormGroup,
          this.statementFormGroup
        )
      )
      .subscribe(() => {
        this.notificationService.success(
          'TTH.STATEMENT.DATA_PROTECTION_CHECK_SUCCESS'
        );
        this.dialogRef.close(true);
      });
  }

  closeDialog() {
    this.dialogRef.close(false);
  }

  downloadFile(fileName: string) {
    this.timetableHearingStatementsService
      .getStatementDocument(this.statement.id!, fileName)
      .subscribe((response) =>
        FileDownloadService.downloadFile(fileName, response)
      );
  }
}
