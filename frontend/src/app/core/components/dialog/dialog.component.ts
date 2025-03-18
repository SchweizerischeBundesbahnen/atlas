import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogClose, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { DialogData } from './dialog.data';
import { CdkScrollable } from '@angular/cdk/scrolling';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-dialog',
    templateUrl: './dialog.component.html',
    imports: [MatDialogClose, CdkScrollable, MatDialogContent, MatDialogActions, TranslatePipe]
})
export class DialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
}
