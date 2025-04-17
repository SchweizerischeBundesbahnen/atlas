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
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
  TimetableFieldNumber,
  TimetableFieldNumbersService,
} from '../../../api';
import { map } from 'rxjs/operators';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { NgIf, NgClass } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { TimetableFieldNumberSelectOptionPipe } from './ttfn-select-option.pipe';

@Component({
  selector: 'ttfn-select',
  templateUrl: './timetable-field-number-select.component.html',
  styleUrls: ['./timetable-field-number-select.component.scss'],
  imports: [
    SearchSelectComponent,
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    NgIf,
    NgClass,
    TranslatePipe,
    TimetableFieldNumberSelectOptionPipe,
  ],
})
export class TimetableFieldNumberSelectComponent
  implements OnInit, OnDestroy, OnChanges
{
  @Input() valueExtraction = 'ttfnid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() required = true;
  @Input() formGroup!: FormGroup;
  @Input() validOn: Date | undefined = undefined;
  @Input() disabled!: boolean;

  @Output() selectedTimetableFieldNumberChanged = new EventEmitter();
  @Output() ttfnSelectionChanged = new EventEmitter<TimetableFieldNumber>();

  timetableFieldNumbers: Observable<TimetableFieldNumber[]> = of([]);
  private formSubscription?: Subscription;

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService
  ) {}

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
