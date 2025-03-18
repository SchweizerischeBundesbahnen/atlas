import { Component } from '@angular/core';
import { MatDialogActions } from '@angular/material/dialog';

@Component({
    selector: 'atlas-dialog-footer',
    templateUrl: './dialog-footer.component.html',
    styleUrls: ['dialog-footer.component.scss'],
    imports: [MatDialogActions]
})
export class DialogFooterComponent {}
