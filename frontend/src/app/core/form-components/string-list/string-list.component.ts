import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ValidatorFn,
  ReactiveFormsModule,
} from '@angular/forms';
import { FieldExample } from '../text-field/field-example';
import { concat, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { TextFieldComponent } from '../text-field/text-field.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { NgClass, NgFor, NgIf, AsyncPipe } from '@angular/common';
import { MatChipSet, MatChip, MatChipRemove } from '@angular/material/chips';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-text-list',
  templateUrl: './string-list.component.html',
  imports: [
    TextFieldComponent,
    ReactiveFormsModule,
    AtlasFieldErrorComponent,
    NgClass,
    MatChipSet,
    NgFor,
    MatChip,
    NgIf,
    MatChipRemove,
    AsyncPipe,
    TranslatePipe,
  ],
})
export class StringListComponent implements OnChanges {
  @Input() formGroup?: FormGroup;
  @Input() formGroupEnabled?: boolean;
  @Input() controlName?: string;
  @Input() maxItems = 10;
  @Input() set itemValidator(validators: ValidatorFn[]) {
    this._inputCtrl.setValidators(validators);
  }
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() placeHolderText!: string;

  showPlaceHolder$: Observable<boolean> = of(false);
  readonly inputCtrlName = 'input';
  private _inputCtrl = new FormControl('');
  readonly strListFormGroup = new FormGroup({
    [this.inputCtrlName]: this._inputCtrl,
  });

  ngOnChanges(changes: SimpleChanges) {
    if (changes.controlName?.firstChange && this.formGroup) {
      this._checkInitialValue();
      if (this.formGroup.enabled) this._handleFormStateChange();
    } else if (changes.formGroup && this.controlName) {
      this._checkInitialValue();
      if (changes.formGroup.currentValue.enabled) this._handleFormStateChange();
    } else if (changes.formGroupEnabled && this.controlName && this.formGroup) {
      if (changes.formGroupEnabled.currentValue) this._handleFormStateChange();
    }
  }

  get strListCtrl(): AbstractControl {
    if (!this.controlName)
      throw new Error('string list control is not defined');
    const ctrl = this.formGroup?.get(this.controlName);
    if (!ctrl) throw new Error('string list control is not defined');
    return ctrl;
  }

  addItem() {
    const inputValue = this._inputCtrl.value;
    if (!inputValue || this._inputCtrl.invalid) return;
    if (!this.strListCtrl.value.includes(inputValue)) {
      this.strListCtrl.setValue([...this.strListCtrl.value, inputValue]);
      this.formGroup!.markAsDirty();
    }
    this._inputCtrl.setValue('');
  }

  removeItem(index: number) {
    const strings: string[] = this.strListCtrl.value;
    strings.splice(index, 1);
    this.strListCtrl.setValue(strings);
    this.strListCtrl.markAsDirty();
  }

  private _checkInitialValue() {
    if (!this.strListCtrl.value)
      throw new Error('initial value should be present for list');
  }

  private _handleFormStateChange() {
    if (this.strListCtrl.value.length === this.maxItems) {
      this._inputCtrl.disable();
    } else {
      this._inputCtrl.enable();
    }
    this.showPlaceHolder$ = this._getShowPlaceHolderObservable();
  }

  private _getShowPlaceHolderObservable() {
    return concat(
      of(this.strListCtrl.value.length === this.maxItems),
      this.strListCtrl.valueChanges.pipe(
        map((val) => {
          if (val.length === this.maxItems) {
            this._inputCtrl?.disable();
            return true;
          } else {
            this._inputCtrl?.enable();
            return false;
          }
        })
      )
    );
  }
}
