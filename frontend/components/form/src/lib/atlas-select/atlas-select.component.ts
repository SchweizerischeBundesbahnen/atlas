import { Component, contentChild, input, output, TemplateRef, viewChild } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { MatOptgroup, MatOption } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import { Field } from '@angular/forms/signals';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

/* eslint-disable  @typescript-eslint/no-explicit-any */
export interface SelectOptionGroup {
  groupValueExtractorProperty: string;
  options: any[];
  valueExtractor: (arg0: any) => any;
}

type SelectFieldType<TYPE> = TYPE | TYPE[] | null;

@Component({
  selector: 'atlas-select',
  templateUrl: './atlas-select.component.html',
  styleUrl: './atlas-select.component.scss',
  imports: [
    NgTemplateOutlet,
    MatSelect,
    MatOption,
    MatOptgroup,
    AtlasLabelFieldComponent,
    AtlasFieldErrorComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class AtlasSelectComponent<TYPE> {
  readonly label = input<string>();
  readonly infoIconTitle = input<string>();
  readonly required = input(false);

  readonly placeHolderLabel = input('FORM.DROPDOWN_PLACEHOLDER');
  readonly optionTranslateLabelPrefix = input<string>();
  readonly multiple = input(false);
  readonly dataCy = input<string>();
  readonly isOptional = input(false);

  readonly options = input<TYPE[]>([]);
  readonly optionsGroup = input<SelectOptionGroup>({
    options: [],
    valueExtractor: (option) => option,
    groupValueExtractorProperty: '',
  });
  readonly valueExtractor = input<(option: TYPE) => any>((option) => option);
  readonly displayExtractor = input<(option: TYPE) => any>((option) => option);

  readonly isSelectAllEnabled = input(false);
  readonly selectAll = input('TTH.ALL_COUNTRIES');

  readonly field = input.required<Field<SelectFieldType<TYPE>>>();

  readonly matOptionPrefix = contentChild<TemplateRef<any>>('matOptionPrefix');
  readonly matOptionGroupPrefix = contentChild<TemplateRef<any>>('matOptionGroupPrefix');

  private readonly allSelected = viewChild<MatOption>('allSelected');

  readonly selectChanged = output<{ value: TYPE[] }>();

  private isAllSelected = false;

  protected getAsObject(option: TYPE): object {
    return { option: option };
  }

  protected onMatSelectionChange(event: MatSelectChange): void {
    this.updateField(event.value);
    this.selectChanged.emit({ value: this.toArray(event.value) });
  }

  protected toggleAllSelection(): void {
    const allSelected = this.allSelected();
    if (allSelected?.selected) {
      const options = this.options();
      this.updateField(options);
      this.selectChanged.emit({ value: options });
      allSelected.select();
      this.isAllSelected = true;
    } else {
      this.updateField([]);
      this.selectChanged.emit({ value: [] });
      this.isAllSelected = false;
    }
  }

  protected deselectAllCheckboxUnlessAllSelected(): void {
    if (!this.isAllSelected) {
      return;
    }
    const allSelected = this.allSelected();
    if (allSelected?.selected) {
      allSelected.deselect();
    }
    const value = this.field()().value();
    if (Array.isArray(value) && value.length === this.options().length) {
      allSelected?.select();
    }
  }

  private updateField(value: SelectFieldType<TYPE>): void {
    const fieldState = this.field()();
    fieldState.value.set(value);
    fieldState.markAsDirty();
    fieldState.markAsTouched();
  }

  private toArray(value: SelectFieldType<TYPE>): TYPE[] {
    if (Array.isArray(value)) {
      return value;
    }
    return value == null ? [] : [value];
  }
}
