import { Component, Input, inject, output, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { StatusChangeData } from '../tth-change-status-dialog/model/status-change-data';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-base-change-dialog',
  templateUrl: './base-change-dialog.component.html',
  styleUrls: ['./base-change-dialog.component.scss'],
  imports: [MatDialogClose, CommentComponent, ReactiveFormsModule, MatDialogActions, TranslatePipe],
  providers: [TranslatePipe],
})
export class BaseChangeDialogComponent {

  @Input() formGroup!: FormGroup;

  @Input() controlName!: string;
  readonly maxChars = input.required<string>();
  readonly changeEvent = output();

  @Input() dialogRef!: MatDialogRef<BaseChangeDialogComponent, boolean>;

  public data: StatusChangeData = inject(MAT_DIALOG_DATA);
  private readonly dialogService = inject(DialogService);

  closeDialog() {
    if (this.formGroup.dirty) {
      this.dialogService.confirmLeave().subscribe((confirm) => {
        if (confirm) {
          this.dialogRef.close(false);
        }
      });
    } else {
      this.dialogRef.close(false);
    }
  }
}
