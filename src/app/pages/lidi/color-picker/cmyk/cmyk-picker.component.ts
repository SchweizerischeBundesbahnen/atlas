import { Component, Input } from '@angular/core';
import { AbstractControl, FormGroup, Validators } from '@angular/forms';
import { CMYK_COLOR_REGEX, ColorService } from '../color.service';

@Component({
  selector: 'app-cmyk-picker [attributeName]',
  templateUrl: './cmyk-picker.component.html',
  styleUrls: ['./cmyk-picker.component.scss', '../color-indicator.scss'],
})
export class CmykPickerComponent {
  @Input() attributeName!: string;
  @Input() label!: string;
  @Input() formGroup!: FormGroup;

  constructor(private colorService: ColorService) {}

  get formControl(): AbstractControl {
    const attributeControl = this.formGroup.get([this.attributeName])!;
    attributeControl.addValidators(Validators.pattern(CMYK_COLOR_REGEX));
    return attributeControl;
  }

  toRbg(cmyk: string) {
    if (!cmyk || !cmyk.match(CMYK_COLOR_REGEX)) {
      return null;
    }

    const splittedCmyk = cmyk.split(',');
    const c = parseInt(splittedCmyk[0]);
    const m = parseInt(splittedCmyk[1]);
    const y = parseInt(splittedCmyk[2]);
    const k = parseInt(splittedCmyk[3]);

    return this.colorService.cmykToHex(c, m, y, k);
  }
}
