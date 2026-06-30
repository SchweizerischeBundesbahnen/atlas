import { Component, ContentChild, input, TemplateRef } from '@angular/core';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { NgTemplateOutlet } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { FieldExample } from '../text-field/field-example';
import { Field, FormField } from '@angular/forms/signals';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';

@Component({
  selector: 'atlas-text-field-sf',
  styleUrl: 'text-field-sf.component.scss',
  imports: [AtlasLabelFieldComponent, NgTemplateOutlet, TranslatePipe, FormField, AtlasFieldErrorSfComponent],
  templateUrl: './text-field-sf.component.html',
})
export class TextFieldSfComponent {
  // Label Field
  readonly fieldLabel = input<string>();
  readonly infoIconTitle = input<string>();
  readonly infoIconLink = input<string>();
  readonly required = input(false);
  readonly fieldExamples = input<FieldExample[]>([]);

  // general
  @ContentChild('customChildInputPostfixTemplate')
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate')
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  customChildInputPrefixTemplate!: TemplateRef<any>;
  readonly paddingBottom = input(true);
  readonly fieldName = input.required<string>();
  readonly placeholder = input('');

  // form
  readonly field = input.required<Field<string>>();
}
