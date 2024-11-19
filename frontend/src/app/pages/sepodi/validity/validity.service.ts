import {Injectable} from '@angular/core';
import { Validity } from '../../model/validity';
import { FormGroup } from '@angular/forms';
import { of, take } from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';

@Injectable()
export class ValidityService {
  validity!: Validity;

  constructor(private dialogService: DialogService) {}

  initValidity(form: FormGroup) {
    this.validity = {
      initValidTo: form?.getRawValue().validTo,
      initValidFrom: form?.getRawValue().validFrom,
      formValidTo: undefined,
      formValidFrom: undefined,
    };
  }

  updateValidity(form: FormGroup) {
    this.validity = {
      ...this.validity,
      formValidTo: form?.getRawValue()?.validTo,
      formValidFrom: form?.getRawValue()?.validFrom,
    };
  }

  confirmValidityDialog() {
    if (
      this.validity.formValidTo!.isSame(this.validity.initValidTo) &&
      this.validity.formValidFrom!.isSame(this.validity.initValidFrom)
    ) {
      return this.dialogService.confirm({
        title: 'DIALOG.CONFIRM_VALIDITY_HAS_NOT_CHANGED_TITLE',
        message: 'DIALOG.CONFIRM_VALIDITY_HAS_NOT_CHANGED',
      });
    }
    return of(true);
  }

  // todo: renames
  validateAndDisableForm() {
    return this.confirmValidityDialog().pipe(take(1));
  }

  validateAndDisableCustom(updateFunction: () => void, disableFunction: () => void) {
    this.confirmValidityDialog()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          disableFunction();
          updateFunction();
        }
      });
  }
}
