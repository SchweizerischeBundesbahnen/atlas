import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent } from '@angular/material/dialog';
import { DialogData } from './dialog.data';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-dialog',
  templateUrl: './dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [MatDialogClose, MatDialogContent, MatDialogActions, TranslatePipe],
  providers: [TranslatePipe],
})
export class DialogComponent {
  public data: DialogData = inject(MAT_DIALOG_DATA);
}
