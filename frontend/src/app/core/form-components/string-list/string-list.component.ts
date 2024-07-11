import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import { FieldExample } from '../text-field/field-example';
import { concat, Observable, of, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'atlas-text-list',
  templateUrl: './string-list.component.html',
})
export class StringListComponent implements OnInit, OnDestroy {
  @Input() set formGroup(form: FormGroup) {
    this._formGroup = form;
    if (this.controlName) this.ngOnInit();
  }
  get formGroup() {
    return this._formGroup!;
  }
  private _formGroup?: FormGroup;

  @Input() controlName!: string;
  @Input() maxItems = 10;
  @Input() itemValidator: ValidatorFn[] = [];

  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() placeHolderText!: string;

  showPlaceHolder$: Observable<boolean> = of(false);
  inputCtrlName?: string;
  private _inputCtrl?: AbstractControl;
  private _sub?: Subscription;

  ngOnInit() {
    if (!this.strListCtrl.value) throw 'initial value should be present for list';
    this.inputCtrlName =
      this.controlName.substring(this.controlName.lastIndexOf('.') + 1) + 'Input';
    this._inputCtrl = new FormControl(
      { value: '', disabled: this.formGroup.disabled },
      this.itemValidator,
    );
    this.formGroup.setControl(this.inputCtrlName, this._inputCtrl);
    this._sub?.unsubscribe();
    this._sub = this.formGroup.statusChanges.subscribe((e) => {
      if (e === 'VALID') {
        if (this.strListCtrl.value.length === this.maxItems) {
          this._inputCtrl?.disable({ emitEvent: false });
        }
        this.showPlaceHolder$ = concat(
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
            }),
          ),
        );
      }
    });
  }

  ngOnDestroy() {
    this._sub?.unsubscribe();
  }

  get strListCtrl(): AbstractControl {
    const ctrl = this.formGroup.get(this.controlName);
    if (!ctrl) throw 'string list control is not defined';
    return ctrl;
  }

  addItem() {
    const inputValue = this._inputCtrl?.value;
    if (inputValue && this._inputCtrl?.valid) {
      if (!this.strListCtrl.value.includes(inputValue)) {
        this.strListCtrl.setValue([...this.strListCtrl.value, inputValue]);
      }
      this._inputCtrl.setValue(undefined);
    }
  }

  removeItem(index: number) {
    const strings: string[] = this.strListCtrl.value;
    strings.splice(index, 1);
    this.strListCtrl.setValue(strings);
    this.strListCtrl.markAsDirty();
  }
}
