import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Observable, of } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';

@Component({
  selector: 'form-search-select',
  templateUrl: './search-select.component.html',
  styleUrls: ['./search-select.component.scss'],
})
export class SearchSelectComponent<TYPE> {
  @Input() items$: Observable<TYPE[]> = of([]);
  @Input() placeholderTextKey = '';
  @Input() controlName!: string;
  @Input() formGroup!: FormGroup;
  @Input() getSelectOption: (item: TYPE) => string = () => '';
  @Input() bindValueInp = '';

  @Output() searchTrigger = new EventEmitter<string>();
  @Output() changeTrigger = new EventEmitter<TYPE>();

  @ViewChild('ngSelect') ngSelect?: NgSelectComponent;

  isDropdownOpen(): boolean {
    return this.ngSelect?.isOpen ?? false;
  }
}
