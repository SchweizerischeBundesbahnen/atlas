import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatOption } from '@angular/material/core';

@Component({
  selector: 'atlas-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.scss'],
})
/* eslint-disable  @typescript-eslint/no-explicit-any */
export class SelectComponent<TYPE> implements OnInit, OnChanges {
  @Input() label: string | undefined;
  @Input() placeHolderLabel = 'FORM.DROPDOWN_PLACEHOLDER';
  @Input() optionTranslateLabelPrefix: string | undefined;

  @Input()
  valueExtractor(option: TYPE): any {
    return option;
  }

  @Input()
  displayExtractor(option: TYPE): any {
    return option;
  }

  @Input() additionalLabelspace = 30;
  @Input() isSelectAllEnabled = false;
  @Input() required = false;

  @Input() selectAll = 'TTH.ALL_COUNTRIES';

  private _disabled = false;
  @Input()
  set disabled(value: boolean) {
    this._disabled = value;
    if (this._isDummyForm) {
      if (this.disabled) {
        this.formGroup.disable();
      } else {
        this.formGroup.enable();
      }
    }
  }

  get disabled(): boolean {
    return this._disabled;
  }

  @Input() multiple = false;

  @Input() dataCy!: string;

  @Input() controlName: string | null = null;
  @Input() formGroup!: FormGroup;

  @Input() options: TYPE[] = [];
  @Input() value: any;

  @ContentChild('matOptionPrefix') matOptionPrefix!: TemplateRef<any>;

  @Output() selectChanged = new EventEmitter();

  @ViewChild('allSelected') private allSelected!: MatOption;

  private _isDummyForm = false;

  private _isAllSelected = false;

  ngOnInit(): void {
    if (!this.formGroup) {
      this.initDummyForm();
    }
    if (this.value) {
      this.getFormControlName()?.setValue(this.value);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.value) {
      this.formGroup?.get(this.controlName!)?.setValue(changes.value.currentValue);
    }
  }

  getAsObject(option: TYPE): object {
    return {
      option: option,
    };
  }

  getFormControlName() {
    return this.formGroup.get(this.controlName!);
  }

  toggleAllSelection() {
    if (this.allSelected.selected) {
      this.getFormControlName()?.setValue(this.options);
      this.selectChanged.emit({ value: this.options });
      this.allSelected.select();
      this._isAllSelected = true;
    } else {
      this.getFormControlName()?.setValue([]);
      this.selectChanged.emit({ value: [] });
    }
  }

  deselectAllCheckboxUnlessAllSelected() {
    if (this._isAllSelected) {
      if (this.allSelected.selected) {
        this.allSelected.deselect();
      }
      if (this.getFormControlName()?.value.length == this.options.length) {
        this.allSelected.select();
      }
    }
  }

  private initDummyForm() {
    this.formGroup = new FormGroup<any>({
      dummy: new FormControl(),
    });
    this.controlName = 'dummy';

    this._isDummyForm = true;
    if (this.disabled) {
      this.formGroup.disable();
    }
  }
}
