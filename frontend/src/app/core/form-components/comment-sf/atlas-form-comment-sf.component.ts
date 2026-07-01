import { Component, input } from '@angular/core';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { MatInput } from '@angular/material/input';
import { Field, FormField } from '@angular/forms/signals';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';

@Component({
  selector: 'atlas-form-comment-sf',
  styleUrl: 'atlas-form-comment-sf.component.scss',
  templateUrl: './atlas-form-comment-sf.component.html',
  imports: [AtlasLabelFieldComponent, MatInput, FormField, AtlasFieldErrorSfComponent],
})
export class AtlasFormCommentSfComponent {
  readonly displayLabel = input(true);
  readonly required = input(false);
  readonly label = input('FORM.COMMENT');
  readonly subLabel = input('FORM.TEXT');
  readonly maxChars = input('1500');
  readonly info = input<string>();

  readonly fieldName = input('comment');
  readonly field = input.required<Field<string>>();
}
