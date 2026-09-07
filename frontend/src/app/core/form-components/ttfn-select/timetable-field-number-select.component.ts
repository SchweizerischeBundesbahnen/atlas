import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  output,
  input,
} from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [ReactiveFormsModule, AtlasLabelFieldComponent, TimetableFieldNumberSelectOptionPipe, SearchSelectComponent],
})
export class TimetableFieldNumberSelectComponent implements OnInit, OnDestroy, OnChanges {
  readonly valueExtraction = input('ttfnid');
  readonly controlName = input.required<string>();
  readonly formModus = input(true);
  readonly required = input(true);
  readonly formGroup = input.required<FormGroup>();
  readonly disabled = input(false);

  readonly selectedTimetableFieldNumberChanged = output();
  readonly ttfnSelectionChanged = output<TimetableFieldNumber>();

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
    const ttfnControl = this.formGroup().get(this.controlName())!;
    this.formSubscription = ttfnControl.valueChanges.subscribe((change) => {
      this.selectedTimetableFieldNumberChanged.emit(change);
      this.resolveReferencedTimetableFieldNumber(change);
    });

    this.resolveReferencedTimetableFieldNumber(ttfnControl.value as string);
  }

  searchTimetableFieldNumber(searchString: string) {
    if (searchString) {
      this.timetableFieldNumbers = this.timetableFieldNumbersService
        .getOverview(
          [searchString],
          undefined,
          undefined,
          undefined,
          undefined,
          undefined,
          undefined,
          ['ttfnid,ASC'],
          undefined,
          true
        )
        .pipe(map((value) => value.objects ?? []));
    }
  }

  private resolveReferencedTimetableFieldNumber(ttfnid: string) {
    if (ttfnid) {
      this.timetableFieldNumbers = this.timetableFieldNumbersService
        .getOverview(undefined, undefined, undefined, undefined, undefined, undefined, undefined, ['ttfnid,ASC'], [
          ttfnid,
        ])
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription?.unsubscribe();
  }
}
