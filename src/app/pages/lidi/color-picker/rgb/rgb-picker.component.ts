import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormGroup, Validators } from '@angular/forms';
import { RGB_HEX_COLOR_REGEX } from '../color.service';

@Component({
  selector: 'app-rgb-picker [attributeName]',
  templateUrl: './rgb-picker.component.html',
  styleUrls: ['./rgb-picker.component.scss'],
})
export class RgbPickerComponent implements OnInit {
  @Input() attributeName!: string;
  @Input() label!: string;
  @Input() formGroup!: FormGroup;

  color = '#FFFFFF';

  ngOnInit(): void {
    this.color = this.formControl?.value;
  }

  onChangeColor(color: string) {
    if (color) {
      this.formControl.patchValue(color);
    } else {
      this.formControl.patchValue(null);
    }
    this.color = this.formControl?.value;
    this.formGroup.markAsDirty();
  }

  get formControl(): AbstractControl {
    const attributeControl = this.formGroup.get([this.attributeName])!;
    attributeControl.addValidators(Validators.pattern(RGB_HEX_COLOR_REGEX));
    return attributeControl;
  }
}
