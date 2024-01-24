import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';

@Component({
  selector: 'atlas-sloid',
  templateUrl: './sloid.component.html',
  styleUrls: ['./sloid.component.scss'],
})
export class SloidComponent implements OnInit {
  @Input() formGroup!: FormGroup;
  @Input() givenParts: string[] = [];
  @Input() givenPrefix?: string;
  @Input() numberColons!: number;

  form!: FormGroup;

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
    this.fixedSloidPart = this.givenPrefix ?? 'ch:1:sloid:' + this.givenParts.join(':') + ':';

    this.initFormgroup();
    this.form.controls.sloid.valueChanges.subscribe((value) => {
      this.patchSloidValue(this.fixedSloidPart + value);
    });
  }

  private patchSloidValue(sloid?: string) {
    this.formGroup.patchValue({ sloid: sloid ? sloid : undefined });
  }

  private initFormgroup() {
    this.form = new FormGroup({
      sloid: new FormControl(null, [
        AtlasCharsetsValidator.colonSeperatedSid4pt(this.numberColons),
      ]),
    });
  }
}
