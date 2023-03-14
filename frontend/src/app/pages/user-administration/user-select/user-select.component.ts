import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  TemplateRef,
} from '@angular/core';
import { debounceTime, distinctUntilChanged, Observable, of, Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';

@Component({
  selector: 'app-user-select',
  templateUrl: './user-select.component.html',
  styleUrls: ['user-select.component.scss'],
})
export class UserSelectComponent<T> implements OnInit, OnDestroy {
  //@Input() form!: FormGroup;
  @Input() searchResults$: Observable<T[]> = of([]);
  @Output() selectionChange: EventEmitter<T> = new EventEmitter<T>();
  @Output() valueChanged: EventEmitter<string> = new EventEmitter<string>();
  @ContentChild(TemplateRef) dropdownElementTemplateRef!: TemplateRef<unknown>;

  displayDropdownElements = false;

  keyUpEvent = new Subject<string>();
  ngOnDestroyEvent = new Subject();

  ngOnInit() {
    this.keyUpEvent
      .pipe(
        takeUntil(this.ngOnDestroyEvent),
        debounceTime(1000),
        distinctUntilChanged(),
        tap((value) => {
          if (value.length > 1) {
            this.valueChanged.emit(value);
          }
        })
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.ngOnDestroyEvent.complete();
  }
}
