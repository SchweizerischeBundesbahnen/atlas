import { ChangeDetectionStrategy, Component, ContentChild, Input, input, TemplateRef } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasLabelFieldComponent, FieldExample } from '@atlas/form';
import { NgTemplateOutlet } from '@angular/common';
import { EmptyToNullDirective } from '../../text-input/empty-to-null';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-text-field',
  templateUrl: './text-field.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./text-field.component.scss'],
  imports: [
    AtlasLabelFieldComponent,
    ReactiveFormsModule,
    NgTemplateOutlet,
    EmptyToNullDirective,
    AtlasFieldErrorComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class TextFieldComponent {
  readonly controlName = input.required<string>();

  @Input() fieldLabel?: string;
  readonly infoIconTitle = input<string>();
  readonly infoIconLink = input<string>();
  readonly required = input(false);
  readonly fieldExamples = input<FieldExample[]>([]);
  readonly paddingBottom = input(true);
  @ContentChild('customChildInputPostfixTemplate')
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate')
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  customChildInputPrefixTemplate!: TemplateRef<any>;
  readonly formGroup = input.required<FormGroup>();
  readonly placeholder = input('');
}
