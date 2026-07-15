import { Component, input } from '@angular/core';
import { MatInput } from '@angular/material/input';
import { Field, FormField } from '@angular/forms/signals';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'atlas-comment',
  styleUrl: 'atlas-comment.component.scss',
  templateUrl: './atlas-comment.component.html',
  imports: [AtlasLabelFieldComponent, MatInput, FormField, AtlasFieldErrorComponent],
})
export class AtlasCommentComponent {
  readonly displayLabel = input(true);
  readonly required = input(false);
  readonly label = input('FORM.COMMENT');
  readonly subLabel = input('FORM.TEXT');
  readonly maxChars = input('1500');
  readonly info = input<string>();

  readonly fieldName = input('comment');
  readonly field = input.required<Field<string>>();
}
