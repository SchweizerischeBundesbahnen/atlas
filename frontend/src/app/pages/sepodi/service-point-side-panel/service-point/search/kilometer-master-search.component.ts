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
import { ServicePointSearchResult, ServicePointsService } from '../../../../../api';
import { Observable, of, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { MatLabel } from '@angular/material/form-field';
import { SplitServicePointNumberPipe } from '../../../../../core/search-service-point/split-service-point-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'kilometer-master-search',
    templateUrl: './kilometer-master-search.component.html',
    styleUrls: ['./kilometer-master-search.component.scss'],
    imports: [SearchSelectComponent, ReactiveFormsModule, MatLabel, SplitServicePointNumberPipe, TranslatePipe]
})
export class KilometerMasterSearchComponent implements OnInit, OnDestroy, OnChanges {
  @Input() valueExtraction = 'number';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() disabled = false;

  @Output() selectedServicePointChanged = new EventEmitter();
  @Output() spSelectionChanged = new EventEmitter<ServicePointSearchResult>();

  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);
  private formSubscription!: Subscription;

  constructor(private readonly servicePointService: ServicePointsService) {}

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
    const spControl = this.formGroup.get(this.controlName)!;
    this.formSubscription = spControl.valueChanges.subscribe((change) => {
      this.selectedServicePointChanged.emit(change);
      this.searchServicePoint(change);
    });

    this.searchServicePoint(spControl.value as string);
  }

  searchServicePoint(searchString: string) {
    if (searchString) {
      this.servicePointSearchResult$ = this.servicePointService
        .searchServicePointsWithRouteNetworkTrue({ value: searchString })
        .pipe(map((values) => values ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription.unsubscribe();
  }
}
