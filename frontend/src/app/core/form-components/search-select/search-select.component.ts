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
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
  NgSelectComponent,
  NgLabelTemplateDirective,
  NgOptionTemplateDirective,
} from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { NgTemplateOutlet, AsyncPipe } from '@angular/common';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'form-search-select',
  templateUrl: './search-select.component.html',
  styleUrls: ['./search-select.component.scss'],
  imports: [
    ReactiveFormsModule,
    NgSelectComponent,
    NgLabelTemplateDirective,
    NgTemplateOutlet,
    NgOptionTemplateDirective,
    AtlasFieldErrorComponent,
    AsyncPipe,
    TranslatePipe,
  ],
})
export class SearchSelectComponent<TYPE> {
  @Input() items$: Observable<TYPE[]> = of([]);
  @Input() multiple = false;
  @Input() placeholderTextKey = '';
  @Input() controlName!: string;
  @Input() formGroup!: FormGroup;
  @Input() bindValueInp = '';
  @Input() pipe?: TranslatePipe;
  @Input() disabled!: boolean;
  @Output() searchTrigger = new EventEmitter<string>();

  @Output() changeTrigger = new EventEmitter<TYPE>();
  @ViewChild('ngSelect') ngSelect?: NgSelectComponent;

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('labelOptionTemplates') labelOptionTemplates!: TemplateRef<any>;

  isDropdownOpen(): boolean {
    return this.ngSelect?.isOpen ?? false;
  }
}
