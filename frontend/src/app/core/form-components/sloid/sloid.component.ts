import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';

@Component({
  selector: 'atlas-sloid',
  templateUrl: './sloid.component.html',
  styleUrls: ['./sloid.component.scss'],
})
export class SloidComponent implements OnInit {
  @Input() formGroup!: FormGroup;
  @Input() givenParts: string[] = [];

  form = new FormGroup({
    sloid: new FormControl(null, [AtlasCharsetsValidator.colonSeperatedNumbers(1)]),
  });

  private _automaticSloid = true;
  get automaticSloid() {
    return this._automaticSloid;
  }

  set automaticSloid(value: boolean) {
    this._automaticSloid = value;
    if (this.automaticSloid) {
      this.patchSloidValue();
    }
  }

  fixedSloidPart!: string;

  ngOnInit() {
    this.fixedSloidPart = 'ch:1:sloid:' + this.givenParts.join(':') + ':';

    this.form.controls.sloid.valueChanges.subscribe((value) => {
      this.patchSloidValue(this.fixedSloidPart + value);
    });
  }

  private patchSloidValue(sloid?: string) {
    this.formGroup.patchValue({ sloid: sloid ? sloid : undefined });
  }
}
