import { Component, ContentChild, Input, TemplateRef, ViewChild, output, input } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'atlas-form-search-select',
  templateUrl: './search-select.component.html',
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
  providers: [TranslatePipe],
})
export class SearchSelectComponent<TYPE> {
  readonly items$ = input<Observable<TYPE[]>>(of([]));
  readonly multiple = input(false);
  readonly placeholderTextKey = input('');
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() controlName!: string;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly bindValueInp = input('');
  readonly pipe = input<TranslatePipe>();
  readonly disabled = input(false);

  protected readonly searchTrigger = output<string>();
  protected readonly searchTriggerSubject = new Subject<string>();

  protected readonly changeTrigger = output<TYPE>();

  @ViewChild('ngSelect') ngSelect?: NgSelectComponent;

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('labelOptionTemplates') labelOptionTemplates!: TemplateRef<any>;

  constructor() {
    this.searchTriggerSubject.asObservable().subscribe({
      next: (value) => this.searchTrigger.emit(value),
    });
  }

  isDropdownOpen(): boolean {
    return this.ngSelect?.isOpen() ?? false;
  }
}
