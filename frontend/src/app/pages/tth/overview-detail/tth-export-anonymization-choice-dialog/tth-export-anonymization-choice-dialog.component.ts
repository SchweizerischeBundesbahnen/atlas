import { Component, inject } from '@angular/core';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'atlas-tth-export-anonymization-choice-dialog',
  templateUrl: './tth-export-anonymization-choice-dialog.component.html',
  imports: [
    DialogCloseComponent,
    TranslatePipe,
    MatRadioButton,
    MatRadioGroup,
    DialogContentComponent,
    DialogFooterComponent,
    FormsModule,
  ],
})
export class TthExportAnonymizationChoiceDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<TthExportAnonymizationChoiceDialogComponent>);

  isAnonymizedExport = true;

  close() {
    this.dialogRef.close(null);
  }

  confirm() {
    this.dialogRef.close({ isAnonymized: this.isAnonymizedExport });
  }
}
