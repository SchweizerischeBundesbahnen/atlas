import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { AbstractControl, FormGroup, Validators } from '@angular/forms';
import { RGB_HEX_COLOR_REGEX } from '../color.service';
import { ColorPickerDirective } from 'ngx-color-picker';
import { Keys } from '../../../../core/constants/keys';

@Component({
  selector: 'app-rgb-picker [attributeName]',
  templateUrl: './rgb-picker.component.html',
  styleUrls: [
    '../color-picker.scss',
    '../color-indicator.scss',
    '../../../../core/form-components/text-field/text-field.component.scss',
  ],
})
export class RgbPickerComponent implements OnInit, OnChanges {
  @ViewChild('colorPicker', { read: ColorPickerDirective })
  colorPickerDirective!: ColorPickerDirective;

  @Input() attributeName!: string;
  @Input() formGroup!: FormGroup;
  @Input() defaultColor!: string;
  @Input() label!: string;
  customError = { errorKey: 'pattern', translationKey: 'VALIDATION.COLOR_INVALID' };

  color = '#FFFFFF';

  constructor(private readonly element: ElementRef) {}

  get formControl(): AbstractControl {
    const attributeControl = this.formGroup.get([this.attributeName])!;
    attributeControl.addValidators(Validators.pattern(RGB_HEX_COLOR_REGEX));
    return attributeControl;
  }

  ngOnInit(): void {
    this.color = this.formControl?.value;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.color = changes.formGroup.currentValue.value[this.attributeName];
  }

  onChangeColor(color: string) {
    if (this.addKeydownEventToColorPicker != undefined) {
      this.addKeydownEventToColorPicker();
    }

    if (color) {
      this.formControl.patchValue(color.toUpperCase());
    } else {
      this.formControl.patchValue(this.defaultColor);
    }
    this.color = this.formControl?.value;
    this.formGroup.markAsDirty();
  }

  closeColorPickerDialog($event: KeyboardEvent) {
    if ($event.key === Keys.TAB) {
      if (this.colorPickerDirective) {
        console.log(this.colorPickerDirective);
        this.colorPickerDirective.closeDialog();
      }
    }
  }

  private addKeydownEventToColorPicker() {
    const colorPickerComponentRef = this.element.nativeElement.querySelector('color-picker');
    if (colorPickerComponentRef) {
      colorPickerComponentRef.tabIndex = 0;
      colorPickerComponentRef.addEventListener('keydown', this.closeColorPickerDialog.bind(this));
    }
  }
}
