import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { ServicePointSearchResult, ServicePointsService } from '../../../../../api';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'service-point-search',
  templateUrl: './service-point-detail-search.component.html',
  styleUrls: ['./service-point-detail-search.component.scss'],
})
export class ServicePointDetailSearchComponent implements OnInit {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() disabled = false;

  @Output() selectedServicePointChanged = new EventEmitter();
  @Output() spSelectionChanged = new EventEmitter<ServicePointSearchResult>();

  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);
  private formSubscription!: Subscription;

  searchInput$ = new Subject<string>();

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
    console.log(searchString);
    if (searchString) {
      this.servicePointSearchResult$ = this.servicePointService
        .searchOnlyBpsServicePoints(true, { value: searchString })
        .pipe(map((values) => values ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription.unsubscribe();
  }
}
