import {
  Component,
  ContentChild,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  TemplateRef,
  ViewChild,
  output,
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatOptgroup, MatOption } from '@angular/material/core';
import { NgTemplateOutlet } from '@angular/common';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { AtlasSpacerComponent } from '../../components/spacer/atlas-spacer.component';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

/* eslint-disable  @typescript-eslint/no-explicit-any */
export interface SelectOptionGroup {
  groupValueExtractorProperty: string;
  options: any[];
  valueExtractor: (arg0: any) => void;
}

@Component({
  selector: 'atlas-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.scss'],
  imports: [
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    AtlasSpacerComponent,
    MatSelect,
    MatOption,
    NgTemplateOutlet,
    MatOptgroup,
    AtlasFieldErrorComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
/* eslint-disable  @typescript-eslint/no-explicit-any */
export class SelectComponent<TYPE> implements OnInit, OnChanges {
  @Input() label: string | undefined;
  @Input() infoIconTitle?: string;
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
  @Input() optionsGroup?: SelectOptionGroup = {
    options: [],
    valueExtractor: Function,
    groupValueExtractorProperty: '',
  };
  @Input() value: any;

  @ContentChild('matOptionPrefix') matOptionPrefix!: TemplateRef<any>;
  @ContentChild('matOptionGroupPrefix') matOptionGroupPrefix!: TemplateRef<any>;

  readonly selectChanged = output<{value: TYPE[]}>();

  @ViewChild('allSelected') private allSelected!: MatOption;

  private _isDummyForm = false;

  private _isAllSelected = false;
  @Input() isOptional = false;

  @Input() groupValueExtractorProperty!: string;

  ngOnInit(): void {
    if (this.optionsGroup!.options.length > 0 && this.options.length > 0) {
      throw new Error('You cannot select both options!!!');
    }
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

  protected onMatSelectionChange(event: MatSelectChange) {
    this.selectChanged.emit({ value: [event.value] });
  }
}
