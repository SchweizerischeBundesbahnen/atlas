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
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'atlas-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.scss'],
})
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

  @Input() additionalLabelspace = true;
  @Input() checkAllEnabled = false;
  @Input() required = false;

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
  @Input() allCountries: TYPE[] = [];
  @Input() value: any;

  @ContentChild('matOptionPrefix') matOptionPrefix!: TemplateRef<any>;

  @Output() selectChanged = new EventEmitter();

  @Output() toggleSelection = new EventEmitter();

  private _isDummyForm = false;

  ngOnInit(): void {
    if (!this.formGroup) {
      this.initDummyForm();
    }
    if (this.value) {
      this.formGroup.get(this.controlName!)?.setValue(this.value);
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

  isAllSelected(): boolean {
    // console.log("isAllSelected is clicked");
    // return true;
    // const returnValue : boolean = this.formGroup.value.dummy && this.options.length
    //   && this.formGroup.value.dummy.length === this.options.length;
    const returnValue: boolean = this.allCountries.length === 76;
    return returnValue;
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
