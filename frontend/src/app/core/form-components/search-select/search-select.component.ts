import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { Observable, of } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';

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
  // TODO: use everywhere individual pipe instead of this function
  @Input() bindValueInp = '';
  @Input() pipe?: TranslatePipe;

  @Output() searchTrigger = new EventEmitter<string>();
  @Output() changeTrigger = new EventEmitter<TYPE>();

  @ViewChild('ngSelect') ngSelect?: NgSelectComponent;
  @ContentChild('labelOptionTemplates') labelOptionTemplates!: TemplateRef<any>;

  isDropdownOpen(): boolean {
    return this.ngSelect?.isOpen ?? false;
  }
}
