import { ChangeDetectionStrategy, Component, input, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';
import { AtlasSlideToggleComponent } from '../atlas-slide-toggle/atlas-slide-toggle.component';
import { TextFieldComponent } from '../text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-sloid',
  templateUrl: './sloid.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [AtlasSlideToggleComponent, TextFieldComponent, ReactiveFormsModule, TranslatePipe],
  providers: [TranslatePipe],
})
export class SloidComponent implements OnInit {
  readonly formGroup = input.required<FormGroup>();
  readonly givenPrefix = input.required<string>();
  readonly numberColons = input.required<number>();

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
    this.initFormGroup();
    this.sloidControl.valueChanges.subscribe((value) => {
      if (value) {
        this.patchSloidValue(this.givenPrefix() + value);
      }
    });
  }

  private patchSloidValue(sloid?: string) {
    this.formGroup().patchValue({ sloid });
  }

  private initFormGroup() {
    this.form = new FormGroup({
      sloid: new FormControl(null),
    });
  }

  get sloidControl() {
    return this.form.controls.sloid;
  }

  private requireValue() {
    this.formGroup().controls.sloid.setValidators([Validators.required]);
    this.formGroup().controls.sloid.updateValueAndValidity();

    this.sloidControl.setValidators([
      Validators.required,
      AtlasCharsetsValidator.colonSeperatedSid4pt(this.numberColons()),
    ]);
    this.sloidControl.markAsTouched();
    this.sloidControl.updateValueAndValidity();
  }

  private automaticValue() {
    this.formGroup().controls.sloid.clearValidators();
    this.formGroup().controls.sloid.updateValueAndValidity();

    this.sloidControl.clearValidators();
    this.sloidControl.updateValueAndValidity();
  }
}
