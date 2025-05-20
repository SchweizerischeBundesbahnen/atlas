import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ServicePointDetailFormGroup } from './service-point-detail-form-group';

@Injectable({
  providedIn: 'root',
})
export class TerminationService {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  private reducedInitialFromValues: any;

  constructor() {}

  initTermination(form: FormGroup<ServicePointDetailFormGroup>) {
    this.reducedInitialFromValues = this.reduceFormGroupToValues(form);
  }

  isStartingTermination(editedForm: FormGroup<ServicePointDetailFormGroup>) {
    const isStopPoint = this.reducedInitialFromValues.stopPoint;
    const isValidated = this.reducedInitialFromValues.status === 'VALIDATED';
    return (
      isStopPoint &&
      isValidated &&
      this.isOnlyValidToChangedInThePast(editedForm)
    );
  }

  private isOnlyValidToChangedInThePast(
    editedForm: FormGroup<ServicePointDetailFormGroup>
  ) {
    const reduceEditedFormToValues = this.reduceFormGroupToValues(editedForm);
    if (
      reduceEditedFormToValues.validTo.isBefore(
        this.reducedInitialFromValues.validTo
      )
    ) {
      //remove validTo property to compare all form values
      delete reduceEditedFormToValues['validTo'];
      delete this.reducedInitialFromValues['validTo'];
      return (
        JSON.stringify(Object.entries(reduceEditedFormToValues).sort()) ===
        JSON.stringify(Object.entries(this.reducedInitialFromValues).sort())
      );
    }
    return false;
  }

  private reduceFormGroupToValues(
    form: FormGroup<ServicePointDetailFormGroup>
    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  ): any {
    return Object.keys(form.controls).reduce(
      // eslint-disable-next-line  @typescript-eslint/no-explicit-any
      (property: any, controlName) => {
        property[controlName] = form.get(controlName)?.value;
        return property;
      },
      {}
    );
  }
}
