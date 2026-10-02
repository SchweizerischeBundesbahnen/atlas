import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MeanOfTransport, ReadServicePointVersion } from '../../../../api';

export const DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN = 3;
export const DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN = 2;

export function resolveDesignationOperationalMaxDigits(servicePointVersions: ReadServicePointVersion[]): number {
  const hasVersionWithMotTrain = servicePointVersions.some((version) =>
    version.meansOfTransport?.includes(MeanOfTransport.Train)
  );
  return hasVersionWithMotTrain
    ? DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
    : DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN;
}

export function designationOperationalMaxDigitsValidator(maxDigits: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (value === null || value === undefined || value === '') {
      return null;
    }
    if (String(value).length <= maxDigits) {
      return null;
    }
    return { designation_operational_max_digits: { maxDigits } };
  };
}
