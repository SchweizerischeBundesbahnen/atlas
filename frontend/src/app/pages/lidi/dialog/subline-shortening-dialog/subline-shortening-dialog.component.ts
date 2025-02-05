import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AffectedSublines } from '../../../../api';

@Component({
  selector: 'app-subline-shortening-dialog',
  templateUrl: './subline-shortening-dialog.component.html',
})
export class SublineShorteningDialogComponent {
  public readonly SUBLINES_URL = '/line-directory/sublines/';

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: { isAllowed: boolean; affectedSublines: AffectedSublines }
  ) {}
}
