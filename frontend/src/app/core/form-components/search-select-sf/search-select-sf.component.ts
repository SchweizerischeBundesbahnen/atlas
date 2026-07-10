import { Component, contentChild, input, output, TemplateRef } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Field } from '@angular/forms/signals';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';

@Component({
  selector: 'atlas-search-select-sf',
  imports: [
    NgLabelTemplateDirective,
    NgOptionTemplateDirective,
    NgSelectComponent,
    NgTemplateOutlet,
    TranslatePipe,
    FormsModule,
    AtlasFieldErrorSfComponent,
  ],
  templateUrl: './search-select-sf.component.html',
})
export class SearchSelectSfComponent<TYPE> {
  readonly items = input<TYPE[]>([]);
  readonly multiple = input(false);
  readonly placeholderTextKey = input('');
  readonly bindValue = input('');
  readonly disabled = input(false);

  readonly field = input.required<Field<TYPE | null>>();

  protected readonly searchTrigger = output<string>();
  protected readonly searchTriggerSubject = new Subject<string>();

  protected readonly changeTrigger = output<TYPE | null>();

  protected readonly labelOptionTemplates = contentChild.required<TemplateRef<any>>('labelOptionTemplates');

  constructor() {
    this.searchTriggerSubject.asObservable().subscribe({
      next: (value) => this.searchTrigger.emit(value),
    });
  }

  protected onSelectionChange(value: TYPE | null) {
    this.field()().value.set(value);
    this.field()().markAsDirty();
    this.field()().markAsTouched();
    this.changeTrigger.emit(value);
  }
}
