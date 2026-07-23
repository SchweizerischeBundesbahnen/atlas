import { Component, contentChild, input, output, TemplateRef } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Field } from '@angular/forms/signals';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'atlas-search-select',
  imports: [
    NgLabelTemplateDirective,
    NgOptionTemplateDirective,
    NgSelectComponent,
    NgTemplateOutlet,
    TranslatePipe,
    FormsModule,
    AtlasFieldErrorComponent,
  ],
  templateUrl: './atlas-search-select.component.html',
})
export class AtlasSearchSelectComponent<TYPE> {
  readonly items = input<TYPE[]>([]);
  readonly multiple = input(false);
  readonly placeholderTextKey = input('');
  readonly bindValue = input('');
  readonly disabled = input(false);
  readonly field = input.required<Field<TYPE | TYPE[] | null>>();

  protected readonly searchTrigger = output<string>();
  protected readonly searchTriggerSubject = new Subject<string>();

  protected readonly changeTrigger = output<TYPE | null>();

  protected readonly labelOptionTemplates = contentChild.required<TemplateRef<unknown>>('labelOptionTemplates');

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
