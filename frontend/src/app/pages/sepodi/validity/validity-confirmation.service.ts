import { Injectable } from '@angular/core';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Observable, of } from 'rxjs';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateService } from '../../../core/date/date.service';
import { ReadServicePointVersion } from '../../../api';
import { Moment } from 'moment';
import {Validity} from "../../model/validity";

@Injectable({ providedIn: 'root' })
export class ValidityConfirmationService {
  constructor(private dialogService: DialogService) {}

  confirmValidityOverServicePoint(
    servicePoint: ReadServicePointVersion[],
    validFrom: Moment,
    validTo: Moment,
  ): Observable<boolean> {
    if (servicePoint.length > 0) {
      const servicePointValidity = VersionsHandlingService.getMaxValidity(servicePoint);
      if (
        validFrom.isBefore(servicePointValidity.validFrom) ||
        validTo.isAfter(servicePointValidity.validTo)
      ) {
        return this.dialogService.confirm({
          title: 'SEPODI.TRAFFIC_POINT_ELEMENTS.VALIDITY_CONFIRMATION.TITLE',
          message: 'SEPODI.TRAFFIC_POINT_ELEMENTS.VALIDITY_CONFIRMATION.MESSAGE',
          messageArgs: {
            validFrom: DateService.getDateFormatted(servicePointValidity.validFrom),
            validTo: DateService.getDateFormatted(servicePointValidity.validTo),
          },
          confirmText: 'COMMON.SAVE_ANYWAY',
          cancelText: 'COMMON.CANCEL',
        });
      }
    }
    return of(true);
  }

  confirmValidity(validity:Validity) {
    if (validity.formValidTo!.isSame(validity.initValidTo) && validity.formValidFrom!.isSame(validity.initValidFrom)) {
      return this.dialogService.confirm({
        title: 'DIALOG.CONFIRM_VALIDITY_HAS_NOT_CHANGED_TITLE',
        message: 'DIALOG.CONFIRM_VALIDITY_HAS_NOT_CHANGED',
      })
    }
    return of(true);
  }
}
