import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AffectedSublines } from '../../../../api';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';

@Component({
  selector: 'app-subline-shortening-dialog',
  templateUrl: './subline-shortening-dialog.component.html',
})
export class SublineShorteningDialogComponent {
  public readonly SUBLINES_URL = '/line-directory/sublines/';

  public get isAllowedOnly() {
    return (
      this.data.affectedSublines.notAllowedSublines?.length! === 0 &&
      this.data.affectedSublines.allowedSublines?.length! > 0
    );
  }

  public get isAllowedAndNotAllowed() {
    return (
      this.data.affectedSublines.notAllowedSublines?.length! > 0 &&
      this.data.affectedSublines.allowedSublines?.length! > 0
    );
  }

  public get isNotAllowedOnly() {
    return (
      this.data.affectedSublines.notAllowedSublines?.length! > 0 &&
      this.data.affectedSublines.allowedSublines?.length! === 0
    );
  }

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: { isAllowed: boolean; affectedSublines: AffectedSublines },
    private router: Router
  ) {}

  openNewTabOfSubline(slnid: string) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.LIDI.path, Pages.SUBLINES.path, slnid])
    );
    window.open(url, '_blank');
  }
}
