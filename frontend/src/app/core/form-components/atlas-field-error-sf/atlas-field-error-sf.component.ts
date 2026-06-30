import { Component, input } from '@angular/core';
import { Field } from '@angular/forms/signals';

@Component({
  selector: 'atlas-field-error-sf',
  styleUrl: 'atlas-field-error-sf.component.scss',
  templateUrl: './atlas-field-error-sf.component.html',
})
export class AtlasFieldErrorSfComponent<TValue> {
  field = input.required<Field<TValue>>();
}
