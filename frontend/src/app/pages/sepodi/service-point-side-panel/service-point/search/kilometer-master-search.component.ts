import { ChangeDetectionStrategy, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, inject, output, input } from '@angular/core';
import { ServicePointSearchResult } from '../../../../../api';
import { Observable, of, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { MatLabel } from '@angular/material/form-field';
import { SplitServicePointNumberPipe } from '../../../../../core/search-service-point/split-service-point-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { ServicePointInternalService } from '../../../../../api/service/sepodi/service-point-internal.service';

@Component({
  selector: 'atlas-kilometer-master-search',
  templateUrl: './kilometer-master-search.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [SearchSelectComponent, ReactiveFormsModule, MatLabel, SplitServicePointNumberPipe, TranslatePipe],
  providers: [TranslatePipe],
})
export class KilometerMasterSearchComponent implements OnInit, OnDestroy, OnChanges {
  private readonly servicePointInternalService = inject(ServicePointInternalService);

  readonly valueExtraction = input('number');

  @Input() controlName!: string;
  readonly formModus = input(true);

  @Input() formGroup!: FormGroup;
  readonly disabled = input(false);

  readonly selectedServicePointChanged = output();
  readonly spSelectionChanged = output<ServicePointSearchResult>();

  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);
  private formSubscription!: Subscription;

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
      this.servicePointSearchResult$ = this.servicePointInternalService
        .searchServicePointsWithRouteNetworkTrue({ value: searchString })
        .pipe(map((values) => values ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription.unsubscribe();
  }
}
