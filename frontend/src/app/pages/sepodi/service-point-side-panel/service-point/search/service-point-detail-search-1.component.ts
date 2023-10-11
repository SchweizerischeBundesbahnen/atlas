import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { ServicePointSearchResult, ServicePointsService } from '../../../../../api';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { FormGroup } from '@angular/forms';

class ServicePointSearchType {
  public static readonly servicePoint = 'SERVICE_POINT';
  public static readonly servicePointBps = 'SERVICE_POINT_BPS';
}

@Component({
  selector: 'service-point-search-1',
  templateUrl: './service-point-detail-search-1.component.html',
  styleUrls: ['./service-point-detail-search-1.component.scss'],
})
export class ServicePointDetailSearch1Component implements OnInit {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() disabled = false;

  @Output() selectedBusinessOrganisationChanged = new EventEmitter();
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
    const boControl = this.formGroup.get(this.controlName)!;
    this.formSubscription = boControl.valueChanges.subscribe((change) => {
      this.selectedBusinessOrganisationChanged.emit(change);
      this.searchServicePoint(change);
    });

    this.searchServicePoint(boControl.value as string);
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
