import { Component, computed, contentChild, input, output, TemplateRef } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'atlas-search-select-sf',
  imports: [NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent, NgTemplateOutlet, TranslatePipe, FormsModule],
  templateUrl: './search-select-sf.component.html',
})
export class SearchSelectSfComponent<TYPE> {
  readonly items = input<TYPE[]>([]);
  readonly multiple = input(false);
  readonly placeholderTextKey = input('');
  readonly bindValue = input('');
  readonly disabled = input(false);
  readonly selectedItem = input<TYPE | null>(null);

  // todo: find a solution to interact with form field,
  //  goals: changing input (can only be TYPE obj) => should be correctly displayed
  //  form edits should be detected for error-field
  protected readonly selectedValue = computed(() => {
    const item = this.selectedItem();
    if (item == null) return null;
    const bv = this.bindValue();
    return bv ? (item as Record<string, unknown>)[bv] : item;
  });

  protected readonly searchTrigger = output<string>();
  protected readonly searchTriggerSubject = new Subject<string>();

  protected readonly changeTrigger = output<TYPE>();

  protected readonly labelOptionTemplates = contentChild.required<TemplateRef<any>>('labelOptionTemplates');

  constructor() {
    this.searchTriggerSubject.asObservable().subscribe({
      next: (value) => this.searchTrigger.emit(value),
    });
  }
}
