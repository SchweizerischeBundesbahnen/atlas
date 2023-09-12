import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'form-search-select',
  templateUrl: './search-select.component.html',
  styleUrls: ['./search-select.component.scss'],
})
export class SearchSelectComponent<TYPE> {
  @Input() items$: Observable<any[]> = of([]);
  @Input() multiple = false;
  @Input() placeholderTextKey = '';
  @Input() controlName!: string;
  @Input() formGroup!: FormGroup;
  @Input() bindValueInp = '';
  @Input() pipe?: TranslatePipe;
  @Input() disabled!: boolean;

  @Input() typeahead = new Subject<string>();
  @Input() loading = false;

  @Output() searchTrigger = new EventEmitter<string>();

  @Output() changeTrigger = new EventEmitter<TYPE>();
  @ViewChild('ngSelect') ngSelect?: NgSelectComponent;
  @ContentChild('labelOptionTemplates') labelOptionTemplates!: TemplateRef<any>;

  isDropdownOpen(): boolean {
    return this.ngSelect?.isOpen ?? false;
  }
}
