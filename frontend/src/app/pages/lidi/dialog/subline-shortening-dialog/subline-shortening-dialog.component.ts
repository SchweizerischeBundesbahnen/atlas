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
  //TODO check if logic can be used from affectesSublines "isAllowed" & "zeroAffectedSublines"
  public get isAllowedOnly() {
    return (
      this.data.affectedSublines.notAllowedSublines?.length! === 0 &&
      this.data.isAllowed
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
      !this.data.isAllowed
    );
  }

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAllowed: boolean;
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
