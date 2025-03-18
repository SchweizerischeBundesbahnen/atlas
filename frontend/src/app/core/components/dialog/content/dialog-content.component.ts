import { Component } from '@angular/core';
import { CdkScrollable } from '@angular/cdk/scrolling';
import { MatDialogContent } from '@angular/material/dialog';

@Component({
    selector: 'atlas-dialog-content',
    templateUrl: './dialog-content.component.html',
    styleUrls: ['dialog-content.component.scss'],
    imports: [CdkScrollable, MatDialogContent]
})
export class DialogContentComponent {}
