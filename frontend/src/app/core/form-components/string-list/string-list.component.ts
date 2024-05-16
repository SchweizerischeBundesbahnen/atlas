import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormControl, FormGroup, ValidatorFn} from "@angular/forms";
import {FieldExample} from "../text-field/field-example";

@Component({
  selector: 'atlas-text-list',
  templateUrl: './string-list.component.html'
})
export class StringListComponent implements OnInit, OnChanges {

  @Input() formGroup!: FormGroup;
  @Input() controlName!: string;
  @Input() maxItems = 10;
  @Input() itemValidator!: ValidatorFn[];

  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() placeHolderText!: string;
  showPlaceHolder = false

  _form!: FormGroup;
  _strings: string[] = [];

  ngOnInit() {
    this.init();
  }

  ngOnChanges() {
    this.init();
  }

  private init() {
    this.showPlaceHolder = false;
    this._form = new FormGroup({
      input: new FormControl('', this.itemValidator)
    });
    this.syncFormEnable();
    this.formGroup.statusChanges.subscribe(() => this.syncFormEnable());
    this._strings = Object.assign([], (this.formControl.value ?? []));
  }

  private syncFormEnable() {
    if (this.formGroup.enabled) {
      this._form.enable();
    } else {
      this._form.disable();
    }
    this.handleMaxInput();
  }

  get formControl() {
    return this.formGroup.get(this.controlName)!;
  }

  get inputFormControl() {
    return this._form.controls.input!;
  }

  addItem() {
    const inputValue = this.inputFormControl.value;
    if (inputValue && this._form.valid) {
      if (!this._strings.includes(inputValue)) {
        this._strings.push(inputValue);
      }
      this.inputFormControl.setValue(undefined);

      this.formControl.setValue(this._strings);
      this.formGroup.markAsDirty();

      this.handleMaxInput();
    }
  }

  removeItem(index: number) {
    this._strings.splice(index, 1);
    this.formControl.setValue(this._strings);
    this.formGroup.markAsDirty();

    this.handleMaxInput();
  }

  private handleMaxInput() {
    if (this.formControl.enabled) {
      if (this._strings.length == this.maxItems) {
        this.showPlaceHolder = true;
        this.inputFormControl.disable();
      } else {
        this.showPlaceHolder = false;
        this.inputFormControl.enable();
      }
    }
  }
}

