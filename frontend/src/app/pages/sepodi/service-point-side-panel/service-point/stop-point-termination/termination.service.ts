import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ServicePointDetailFormGroup } from '../service-point-detail-form-group';
import { environment } from '../../../../../../environments/environment';
import { Country, ReadServicePointVersion } from '../../../../../api';
import moment from 'moment';

export const ALLOWED_TERMINATION_COUNTRIES: Country[] = [
  Country.Switzerland,
  Country.GermanyBus,
  Country.AustriaBus,
  Country.ItalyBus,
  Country.FranceBus,
];

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
    const isStopPointCountryAllowed =
      this.isStopPointCountryTerminationAllowed();
    const isValidated = this.reducedInitialFromValues.status === 'VALIDATED';
    const isInThePast = this.isOnlyValidToChangedInThePast(editedForm);
    return (
      isStopPoint && isValidated && isStopPointCountryAllowed && isInThePast
    );
  }

  private isStopPointCountryTerminationAllowed() {
    return ALLOWED_TERMINATION_COUNTRIES.some(
      (country) => this.reducedInitialFromValues.country === country
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
