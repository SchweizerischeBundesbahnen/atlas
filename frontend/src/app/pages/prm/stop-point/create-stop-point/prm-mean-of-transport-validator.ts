import { AbstractControl, ValidationErrors } from '@angular/forms';
import { MeanOfTransport } from '../../../../api';
import { PrmMeanOfTransportHelper } from '../../prm-mean-of-transport-helper';

export class PrmMeanOfTransportValidator {
  static isReducedOrComplete(control: AbstractControl): ValidationErrors | null {
    const selectedMeansOfTransport: MeanOfTransport[] = control.value;
    const { hasReduced, hasComplete } =
      PrmMeanOfTransportHelper.getReducedCompleteInstances(selectedMeansOfTransport);
    if (hasReduced && hasComplete) {
      return { meansOfTransportWrongCombination: control.value };
    }
    return null;
  }
}
