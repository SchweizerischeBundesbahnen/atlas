import { Component, ContentChild, Input, input, TemplateRef } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { FieldExample } from './field-example';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { NgTemplateOutlet } from '@angular/common';
import { EmptyToNullDirective } from '../../text-input/empty-to-null';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-text-field',
  templateUrl: './text-field.component.html',
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
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() controlName!: string;
  // TODO: Skipped for migration because:
  //  This input is used in a control flow expression (e.g. `@if` or `*ngIf`)
  //  and migrating would break narrowing currently.
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
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly placeholder = input('');
}
