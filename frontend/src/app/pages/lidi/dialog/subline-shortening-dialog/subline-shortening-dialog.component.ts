import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AffectedSublinesModel } from '../../../../api';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

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

  get notice() {
    if (this.isValidFromShortened && this.isValidToShortened) {
      return {
        key: 'LIDI.SUBLINE_SHORTENING.ALLOWED.SHORT_AUTOMATICALLY_NOTICE_BOTH',
        params: { validFrom: this.validFrom, validTo: this.validTo },
      };
    } else if (this.isValidFromShortened) {
      return {
        key: 'LIDI.SUBLINE_SHORTENING.ALLOWED.SHORT_AUTOMATICALLY_NOTICE',
        params: {
          validity: this.translateService.instant('COMMON.VALID_FROM'),
          date: this.validFrom,
        },
      };
    } else if (this.isValidToShortened) {
      return {
        key: 'LIDI.SUBLINE_SHORTENING.ALLOWED.SHORT_AUTOMATICALLY_NOTICE',
        params: {
          validity: this.translateService.instant('COMMON.VALID_TO'),
          date: this.validTo,
        },
      };
    }
    return { key: '', params: {} };
  }

  public get isValidFromShortened() {
    return this.data.isValidFromShortened;
  }

  public get isValidToShortened() {
    return this.data.isValidToShortened;
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
      isValidFromShortened: boolean;
      isValidToShortened: boolean;
    },
    private router: Router,
    private datePipe: DatePipe,
    private translateService: TranslateService
  ) {}

  openNewTabOfSubline(slnid: string) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.LIDI.path, Pages.SUBLINES.path, slnid])
    );
    window.open(url, '_blank');
  }
}
