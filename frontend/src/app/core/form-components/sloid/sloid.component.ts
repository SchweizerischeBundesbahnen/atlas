import { Component, Input, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';
import { AtlasSlideToggleComponent } from '../atlas-slide-toggle/atlas-slide-toggle.component';
import { NgIf } from '@angular/common';
import { TextFieldComponent } from '../text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-sloid',
  templateUrl: './sloid.component.html',
  styleUrls: ['./sloid.component.scss'],
  imports: [
    AtlasSlideToggleComponent,
    NgIf,
    TextFieldComponent,
    ReactiveFormsModule,
    TranslatePipe,
  ],
})
export class SloidComponent implements OnInit {
  @Input() formGroup!: FormGroup;
  @Input() givenPrefix!: string;
  @Input() numberColons!: number;

  form!: FormGroup;

  private _automaticSloid = true;
  get automaticSloid() {
    return this._automaticSloid;
  }

  set automaticSloid(value: boolean) {
    this._automaticSloid = value;
    if (this.automaticSloid) {
      this.automaticValue();
      this.patchSloidValue();
    } else {
      this.requireValue();
    }
  }

  ngOnInit() {
    this.initFormgroup();
    this.sloidControl.valueChanges.subscribe((value) => {
      if (value) {
        this.patchSloidValue(this.givenPrefix + value);
      }
    });
  }

  private patchSloidValue(sloid?: string) {
    this.formGroup.patchValue({ sloid: sloid ? sloid : undefined });
  }

  private initFormgroup() {
    this.form = new FormGroup({
      sloid: new FormControl(null),
    });
  }

  get sloidControl() {
    return this.form.controls.sloid;
  }

  private requireValue() {
    this.formGroup.controls.sloid.setValidators([Validators.required]);
    this.formGroup.controls.sloid.updateValueAndValidity();

    this.sloidControl.setValidators([
      Validators.required,
      AtlasCharsetsValidator.colonSeperatedSid4pt(this.numberColons),
    ]);
    this.sloidControl.markAsTouched();
    this.sloidControl.updateValueAndValidity();
  }

  private automaticValue() {
    this.formGroup.controls.sloid.clearValidators();
    this.formGroup.controls.sloid.updateValueAndValidity();

    this.sloidControl.clearValidators();
    this.sloidControl.updateValueAndValidity();
  }
}
