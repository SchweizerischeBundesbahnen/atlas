import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AffectedSublinesModel } from '../../../../api';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-subline-shortening-dialog',
  templateUrl: './subline-shortening-dialog.component.html',
  providers: [DatePipe],
})
export class SublineShorteningDialogComponent {
  public get hasAllowedOnly() {
    return (
      !this.data.affectedSublines.hasNotAllowedSublinesOnly &&
      this.data.affectedSublines.hasAllowedSublinesOnly
    );
  }

  public get hasAllowedAndNotAllowed() {
    return (
      this.data.affectedSublines.hasAllowedSublinesOnly &&
      this.data.affectedSublines.hasNotAllowedSublinesOnly
    );
  }

  public get hasNotAllowedOnly() {
    return (
      this.data.affectedSublines.hasNotAllowedSublinesOnly &&
      !this.data.affectedSublines.hasAllowedSublinesOnly
    );
  }

  public get validFrom() {
    return this.datePipe.transform(new Date(this.data.validFrom), 'dd.MM.yyyy');
  }

  public get validTo() {
    return this.datePipe.transform(new Date(this.data.validTo), 'dd.MM.yyyy');
  }

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAllowedToShort: boolean;
      affectedSublines: AffectedSublinesModel;
      validFrom: Date;
      validTo: Date;
    },
    private router: Router,
    private datePipe: DatePipe
  ) {}

  openNewTabOfSubline(slnid: string) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.LIDI.path, Pages.SUBLINES.path, slnid])
    );
    window.open(url, '_blank');
  }
}
