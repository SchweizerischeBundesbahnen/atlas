import { AbstractControl, FormGroup } from '@angular/forms';

export function addControlToFormNoEvent<
  T extends {
    [K in keyof T]: AbstractControl;
  },
>(
  form: FormGroup<T>,
  controlName: string & keyof T,
  control: Required<T>[string & keyof T]
) {
  form.addControl(controlName, control, { emitEvent: false });
}

export function removeControlFromFormNoEvent<
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
