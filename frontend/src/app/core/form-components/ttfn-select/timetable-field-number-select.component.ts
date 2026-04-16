import { Component, EventEmitter, inject, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, } from '@angular/core';
import { Observable, of, Subscription } from 'rxjs';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TimetableFieldNumber } from '../../../api';
import { map } from 'rxjs/operators';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { TimetableFieldNumberSelectOptionPipe } from './ttfn-select-option.pipe';
import { SearchSelectComponent } from '../search-select/search-select.component';

@Component({
  selector: 'atlas-ttfn-select',
  templateUrl: './timetable-field-number-select.component.html',
  imports: [ReactiveFormsModule, AtlasLabelFieldComponent, TimetableFieldNumberSelectOptionPipe, SearchSelectComponent],
})
export class TimetableFieldNumberSelectComponent implements OnInit, OnDestroy, OnChanges {
  @Input() valueExtraction = 'ttfnid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() required = true;
  @Input() formGroup!: FormGroup;
  @Input() validOn: Date | undefined = undefined;
  @Input() disabled!: boolean;

  @Output() selectedTimetableFieldNumberChanged = new EventEmitter();
  @Output() ttfnSelectionChanged = new EventEmitter<TimetableFieldNumber>();

  private readonly timetableFieldNumbersService = inject(TimetableFieldNumberInternalService);

  timetableFieldNumbers: Observable<TimetableFieldNumber[]> = of([]);
  private formSubscription?: Subscription;

  ngOnInit(): void {
    this.init();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.formGroup) {
      if (this.formSubscription) {
        this.formSubscription.unsubscribe();
      }
      this.init();
    }
  }

  init() {
    const ttfnControl = this.formGroup.get(this.controlName)!;
    this.formSubscription = ttfnControl.valueChanges.subscribe((change) => {
      this.selectedTimetableFieldNumberChanged.emit(change);
      this.searchTimetableFieldNumber(change);
    });

    this.searchTimetableFieldNumber(ttfnControl.value as string);
  }

  searchTimetableFieldNumber(searchString: string) {
    if (searchString) {
      this.timetableFieldNumbers = this.timetableFieldNumbersService
        .getOverview([searchString], undefined, undefined, this.validOn, undefined, undefined, undefined, [
          'ttfnid,ASC',
        ])
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription?.unsubscribe();
  }
}
