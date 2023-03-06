import { Component, ContentChild, Input, OnInit, TemplateRef } from '@angular/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { FieldExample } from './field-example';
import { FieldDatePicker } from './field-date-picker';
import { AtlasFieldCustomError } from '../atlas-field-error/atlas-field-custom-error';
import { RgbPicker } from '../../../pages/lidi/color-picker/rgb/rgb-picker';

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
  @Input() rgbPicker!: RgbPicker;
  @Input() customInputNgStyle!: Record<string, string | undefined | null>;
  @Input() customError!: AtlasFieldCustomError;
  @ContentChild('customChildInputPostfixTemplate')
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate') customChildInputPrefixTemplate!: TemplateRef<any>;

  form: FormGroup = new FormGroup({});

  constructor(private readonly rootFormGroup: FormGroupDirective) {}

  ngOnInit() {
    this.form = this.rootFormGroup.control;
  }

  onChangeColor(color: string) {
    this.rgbPicker.onChangeColor(color);
  }

  closeColorPickerDialog($event: KeyboardEvent) {
    this.rgbPicker.closeColorPickerDialog($event);
  }
}
