import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FieldExample } from './field-example';
import { AtlasFieldCustomError } from '../atlas-field-error/atlas-field-custom-error';

@Component({
  selector: 'atlas-text-field',
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.scss'],
})
export class TextFieldComponent {
  @Input() controlName!: string;
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() customInputNgStyle!: Record<string, string | undefined | null>;
  @Input() customError!: AtlasFieldCustomError;
  @ContentChild('customChildInputPostfixTemplate')
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate') customChildInputPrefixTemplate!: TemplateRef<any>;
  @Input() formGroup!: FormGroup;
  @Input() placeholder = '';
}
