import { Component, contentChild, input, TemplateRef } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { FieldExample } from './field-example';
import { Field, FormField } from '@angular/forms/signals';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'atlas-text-field',
  styleUrl: 'atlas-text-field.component.scss',
  imports: [AtlasLabelFieldComponent, NgTemplateOutlet, TranslatePipe, FormField, AtlasFieldErrorComponent],
  templateUrl: './atlas-text-field.component.html',
})
export class AtlasTextFieldComponent {
  // label-field
  readonly fieldLabel = input<string>();
  readonly infoIconTitle = input<string>();
  readonly infoIconLink = input<string>();
  readonly required = input(false);
  readonly fieldExamples = input<FieldExample[]>([]);

  // general
  readonly customChildInputPostfixTemplate = contentChild<TemplateRef<unknown>>('customChildInputPostfixTemplate');
  readonly customChildInputPrefixTemplate = contentChild<TemplateRef<unknown>>('customChildInputPrefixTemplate');
  readonly paddingBottom = input(true);
  readonly fieldName = input.required<string>();
  readonly placeholder = input('');

  // form
  readonly field = input.required<Field<string>>();
}
