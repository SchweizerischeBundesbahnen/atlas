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

@Component({
  selector: 'app-rgb-picker [attributeName]',
  templateUrl: './rgb-picker.component.html',
  styleUrls: ['./rgb-picker.component.scss', '../color-indicator.scss'],
})
export class RgbPickerComponent implements OnInit, OnChanges {
  @ViewChild('input') someInput!: ElementRef;
  @Input() attributeName!: string;
  @Input() label!: string;
  @Input() formGroup!: FormGroup;

  color = '#FFFFFF';

  ngOnInit(): void {
    this.color = this.formControl?.value;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.color = changes.formGroup.currentValue.value[this.attributeName];
  }

  onChangeColor(color: string) {
    if (color) {
      this.formControl.patchValue(color.toUpperCase());
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
