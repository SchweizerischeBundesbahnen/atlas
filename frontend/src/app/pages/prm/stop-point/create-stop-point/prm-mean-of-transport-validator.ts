import { AbstractControl, ValidationErrors } from '@angular/forms';
import { MeanOfTransport } from '../../../../api';
import { PrmMeanOfTransportHelper } from '../../prm-mean-of-transport-helper';

export class PrmMeanOfTransportValidator {
  static isReducedOrComplete(control: AbstractControl): ValidationErrors | null {
    const selectedMeansOfTransport: MeanOfTransport[] = control.value;
    const { reduced, complete } =
      PrmMeanOfTransportHelper.getReducedCompleteInstances(selectedMeansOfTransport);
    if (reduced.length > 0 && complete.length > 0) {
      return { meansOfTransportWrongCombination: control.value };
    }
    return null;
  }
}
