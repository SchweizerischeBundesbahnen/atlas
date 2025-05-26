import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ServicePointDetailFormGroup } from '../service-point-detail-form-group';
import { environment } from '../../../../../../environments/environment';
import { ReadServicePointVersion } from '../../../../../api';
import moment from 'moment';

@Injectable({
  providedIn: 'root',
})
export class TerminationService {
  private reducedInitialFromValues!: Partial<ReadServicePointVersion>;

  constructor() {}

  initTermination(form: FormGroup<ServicePointDetailFormGroup>) {
    this.reducedInitialFromValues = this.reduceFormGroupToValues(form);
  }

  isStartingTermination(editedForm: FormGroup<ServicePointDetailFormGroup>) {
    if (environment.terminationWorkflowEnabled) {
      return this.checkStartingTermination(editedForm);
    }
    return false;
  }

  private checkStartingTermination(
    editedForm: FormGroup<ServicePointDetailFormGroup>
  ) {
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
      moment(reduceEditedFormToValues.validTo).isBefore(
        this.reducedInitialFromValues.validTo
      )
    ) {
      //remove validTo property to compare all form values
      this.deleteValidToProperty(reduceEditedFormToValues);
      return this.areValuesEquals(
        this.reducedInitialFromValues,
        reduceEditedFormToValues
      );
    }
    return false;
  }

  private areValuesEquals(
    reduceEditedFormToValues: Partial<ReadServicePointVersion>,
    reducedInitialFromValues: Partial<ReadServicePointVersion>
  ) {
    return (
      JSON.stringify(Object.entries(reduceEditedFormToValues).sort()) ===
      JSON.stringify(Object.entries(reducedInitialFromValues).sort())
    );
  }

  private deleteValidToProperty(
    reduceEditedFormToValues: Partial<ReadServicePointVersion>
  ) {
    const validToProperty = 'validTo';
    delete reduceEditedFormToValues[validToProperty];
    delete this.reducedInitialFromValues[validToProperty];
  }

  private reduceFormGroupToValues(
    form: FormGroup<ServicePointDetailFormGroup>
  ): Partial<ReadServicePointVersion> {
    return form.getRawValue() as unknown as ReadServicePointVersion;
  }
}
