import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { Observable, of, Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { TimetableFieldNumber, TimetableFieldNumbersService } from '../../../api';
import { map } from 'rxjs/operators';

@Component({
  selector: 'ttfn-select',
  templateUrl: './timetable-field-number-select.component.html',
  styleUrls: ['./timetable-field-number-select.component.scss'],
})
export class TimetableFieldNumberSelectComponent implements OnInit, OnDestroy, OnChanges {
  @Input() valueExtraction = 'ttfnid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() validOn: Date | undefined = undefined;

  @Output() selectedTimetableFieldNumberChanged = new EventEmitter();
  @Output() ttfnSelectionChanged = new EventEmitter<TimetableFieldNumber>();

  timetableFieldNumbers: Observable<TimetableFieldNumber[]> = of([]);
  private formSubscription?: Subscription;

  constructor(private timetableFieldNumbersService: TimetableFieldNumbersService) {}

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
        .getOverview(
          [searchString],
          undefined,
          undefined,
          this.validOn,
          undefined,
          undefined,
          undefined,
          ['ttfnid,ASC']
        )
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription?.unsubscribe();
  }
}
