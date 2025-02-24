import {
  StopPointDetailFormGroup,
  StopPointFormGroupBuilder,
} from './stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../api';
import { FormGroup } from '@angular/forms';
import moment from "moment";

describe('StopPointFormGroupBuilder', () => {
  describe('complete stop point form', () => {
    it('should get writeable object with interoperable default false', () => {
      const completeForm: FormGroup<StopPointDetailFormGroup> =
        StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
      completeForm.controls.meansOfTransport.setValue([MeanOfTransport.Train]);
      StopPointFormGroupBuilder.populateDropdownsForCompleteWithDefaultValue(
        completeForm
      );
      completeForm.controls.validFrom.setValue(moment());
      completeForm.controls.validTo.setValue(moment());

      const writableStopPoint =
        StopPointFormGroupBuilder.getWritableStopPoint(completeForm);
      expect(writableStopPoint.interoperable).toBeFalse();
    });
  });
});
