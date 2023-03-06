import { Component, ContentChild, Input, OnInit, TemplateRef } from '@angular/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { FieldExample } from './field-example';
import { TranslatePipe } from '@ngx-translate/core';
import { FieldDatePicker } from './field-date-picker';
import { AtlasFieldCustomError } from '../atlas-field-error/atlas-field-custom-error';

@Component({
  selector: 'atlas-text-field',
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.scss'],
})
export class TextFieldComponent implements OnInit {
  @Input() controlName!: string;
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() datePicker!: FieldDatePicker;
  @Input() customInputNgStyle!: Record<string, string | undefined | null>;
  @Input() customError!: AtlasFieldCustomError;
  @ContentChild('customChildInputPostfixTemplate')
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate') customChildInputPrefixTemplate!: TemplateRef<any>;

  form: FormGroup = new FormGroup({});

  constructor(
    private readonly rootFormGroup: FormGroupDirective,
    private readonly translatePipe: TranslatePipe
  ) {}

  ngOnInit() {
    this.form = this.rootFormGroup.control;
  }

  translate(fieldExample: FieldExample): string {
    if (fieldExample.label && !fieldExample.arg) {
      return this.translatePipe.transform(fieldExample.label);
    }
    if (fieldExample.label && fieldExample.arg) {
      return this.translatePipe.transform(fieldExample.label, {
        [fieldExample.arg!.key]: fieldExample.arg?.value,
      });
    }
    return fieldExample.label!;
  }
}
