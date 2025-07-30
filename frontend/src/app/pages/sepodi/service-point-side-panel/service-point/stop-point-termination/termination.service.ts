import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from '../service-point-form/form-group/service-point-detail-form-group';
import { environment } from '../../../../../../environments/environment';
import { Country, CreateServicePointVersion } from '../../../../../api';
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
  private initialFormValues?: Partial<CreateServicePointVersion>;

  initTermination(form: FormGroup<ServicePointDetailFormGroup>) {
    this.initialFormValues =
      ServicePointFormGroupBuilder.mapper.getWritableServicePoint(form);
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
    if (!this.initialFormValues)
      throw Error('initTermination was not called before');
    const isStopPoint: boolean =
      (this.initialFormValues.meansOfTransport?.length ?? 0) > 0;
    const isStopPointCountryAllowed =
      this.isStopPointCountryTerminationAllowed();
    const isValidated = this.initialFormValues.status === 'VALIDATED';
    const isInThePast = this.isOnlyValidToChangedInThePast(editedForm);
    return (
      isStopPoint && isValidated && isStopPointCountryAllowed && isInThePast
    );
  }

  private isStopPointCountryTerminationAllowed() {
    return ALLOWED_TERMINATION_COUNTRIES.some(
      (country) => this.initialFormValues?.country === country
    );
  }

  private isOnlyValidToChangedInThePast(
    editedForm: FormGroup<ServicePointDetailFormGroup>
  ) {
    const editedFormValues =
      ServicePointFormGroupBuilder.mapper.getWritableServicePoint(editedForm);
    if (
      moment(editedFormValues.validTo).isBefore(this.initialFormValues?.validTo)
    ) {
      //remove validTo property to compare all form values
      this.deleteValidToProperty(editedFormValues);
      return this.areValuesEquals(this.initialFormValues!, editedFormValues);
    }
    return false;
  }

  private areValuesEquals(
    initialFormValues: Partial<CreateServicePointVersion>,
    editedFormValues: Partial<CreateServicePointVersion>
  ) {
    const sortFn = (a: [string, unknown], b: [string, unknown]): number =>
      a[0].localeCompare(b[0]);
    return (
      JSON.stringify(Object.entries(initialFormValues).sort(sortFn)) ===
      JSON.stringify(Object.entries(editedFormValues).sort(sortFn))
    );
  }

  private deleteValidToProperty(
    editedFormValues: Partial<CreateServicePointVersion>
  ) {
    const validToProperty = 'validTo';
    delete editedFormValues[validToProperty];
    delete this.initialFormValues![validToProperty];
  }
}
