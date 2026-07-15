import { Component, input } from '@angular/core';
import { Field } from '@angular/forms/signals';

@Component({
  selector: 'atlas-field-error',
  styleUrl: 'atlas-field-error.component.scss',
  templateUrl: './atlas-field-error.component.html',
})
export class AtlasFieldErrorComponent<TValue> {
  field = input.required<Field<TValue>>();
}
