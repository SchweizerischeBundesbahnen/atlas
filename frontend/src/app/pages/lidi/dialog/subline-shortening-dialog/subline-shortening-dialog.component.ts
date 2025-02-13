import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AffectedSublinesModel } from '../../../../api';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';

@Component({
  selector: 'app-subline-shortening-dialog',
  templateUrl: './subline-shortening-dialog.component.html',
})
export class SublineShorteningDialogComponent {
  public get hasAllowedOnly() {
    return (
      !this.data.affectedSublines.hasNotAllowedSublinesToShortOnly &&
      this.data.affectedSublines.hasAllowedSublinesToShortOnly
    );
  }

  public get hasAllowedAndNotAllowed() {
    return (
      this.data.affectedSublines.hasAllowedSublinesToShortOnly &&
      this.data.affectedSublines.hasNotAllowedSublinesToShortOnly
    );
  }

  public get hasNotAllowedOnly() {
    return (
      this.data.affectedSublines.hasNotAllowedSublinesToShortOnly &&
      !this.data.affectedSublines.hasAllowedSublinesToShortOnly
    );
  }

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAllowedToShort: boolean;
      affectedSublines: AffectedSublinesModel;
    },
    private router: Router
  ) {}

  openNewTabOfSubline(slnid: string) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.LIDI.path, Pages.SUBLINES.path, slnid])
    );
    window.open(url, '_blank');
  }
}
