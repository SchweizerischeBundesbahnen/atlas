import { FormGroup } from '@angular/forms';
import { ServicePointDetailFormGroup } from './service-point-detail-form-group';

export class TerminationHelper {
  public static excludeValidToAndCheckIfValuesAreEquals(
    form: FormGroup<ServicePointDetailFormGroup>,
    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
    initialFromValues: any
  ) {
    const reduceFormGroupToValues = this.reduceFormGroupToValues(form);
    //remove validTo property to compare all form values
    delete reduceFormGroupToValues['validTo'];
    delete initialFromValues['validTo'];
    return (
      JSON.stringify(Object.entries(reduceFormGroupToValues).sort()) ===
      JSON.stringify(Object.entries(initialFromValues).sort())
    );
  }

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  public static reduceFormGroupToValues(formGroup: FormGroup): any {
    return Object.keys(formGroup.controls).reduce(
      // eslint-disable-next-line  @typescript-eslint/no-explicit-any
      (property: any, controlName) => {
        property[controlName] = formGroup.get(controlName)?.value;
        return property;
      },
      {}
    );
  }
}
