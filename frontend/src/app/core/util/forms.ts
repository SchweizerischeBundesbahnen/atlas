import { AbstractControl, FormGroup } from '@angular/forms';

export function addGroupToForm<
  T extends {
    [K in keyof T]: AbstractControl;
  },
>(
  form: FormGroup<T>,
  controlName: string & keyof T,
  group: Required<T>[string & keyof T]
) {
  form.addControl(controlName, group, { emitEvent: false });
}

export function removeGroupFromForm<
  T extends {
    [K in keyof T]: AbstractControl;
  },
>(
  form: FormGroup<T>,
  controlName: {
    [K in keyof T]-?: undefined extends T[K] ? K : never;
  }[keyof T] &
    string
) {
  form.removeControl(controlName, { emitEvent: false });
}
