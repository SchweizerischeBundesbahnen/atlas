import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormControl, FormGroup, ValidatorFn} from "@angular/forms";

@Component({
  selector: 'atlas-text-list',
  templateUrl: './string-list.component.html',
})
export class StringListComponent implements OnInit, OnChanges {

  @Input() formGroup!: FormGroup;
  @Input() controlName!: string;
  @Input() maxItems = 10;
  @Input() itemValidator!: ValidatorFn[];

  _form!: FormGroup;
  _strings: string[] = [];

  ngOnInit() {
    this.init();
  }

  ngOnChanges() {
    this.init();
  }

  private init() {
    this._form = new FormGroup({
      input: new FormControl('', this.itemValidator)
    });
    this.syncFormEnable();
    this.formGroup.statusChanges.subscribe(() => this.syncFormEnable());
    this._strings = this.formControl.value ?? [];
  }

  private syncFormEnable() {
    if (this.formGroup.enabled) {
      this._form.enable();
    } else {
      this._form.disable();
    }
  }

  get formControl() {
    return this.formGroup.get(this.controlName)!;
  }

  get inputFormControl() {
    return this._form.controls.input!;
  }

  addItem() {
    const inputValue = this.inputFormControl.value;
    if (inputValue) {
      if (!this._strings.includes(inputValue)) {
        this._strings.push(inputValue);
      }
      this.inputFormControl.setValue(undefined);

      this.formControl.setValue(this._strings);
      this.formGroup.markAsDirty();
    }
  }

  removeItem(index: number) {
    this._strings.splice(index, 1);
    this.formControl.setValue(this._strings);
    this.formGroup.markAsDirty();
  }
}

