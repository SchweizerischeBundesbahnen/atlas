import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ServicePointSearchResult, ServicePointsService } from '../../../api';
import { FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-search-service-point',
  templateUrl: './search-service-point.component.html',
  styleUrls: ['./search-service-point.component.scss'],
})
export class SearchServicePointComponent {
  constructor(
    private readonly router: Router,
    private route: ActivatedRoute,
    private readonly servicePointService: ServicePointsService
  ) {}

  private _searchValue!: string;

  get searchValue(): string {
    return this._searchValue;
  }

  @Input() form!: FormGroup;
  @Output() selectionChange: EventEmitter<ServicePointSearchResult> =
    new EventEmitter<ServicePointSearchResult>();
  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);
  navigateToServicePoint(searchResultSelected: ServicePointSearchResult) {
    //todo: do not show search component when detail mode
    this.router
      .navigate([Pages.SERVICE_POINTS.path, searchResultSelected.number?.number], {
        relativeTo: this.route,
      })
      .then();
  }
  searchServicePoint(value: string): void {
    this._searchValue = value;
    if (!value) {
      return;
    }
    this.servicePointSearchResult$ = this.servicePointService.searchServicePoints(value);
  }
}
