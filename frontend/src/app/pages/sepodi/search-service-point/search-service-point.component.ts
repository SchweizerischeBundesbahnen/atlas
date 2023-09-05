import { Component } from '@angular/core';
import { ServicePointSearchResult, ServicePointsService } from '../../../api';
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

  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);

  navigateToServicePoint(searchResultSelected: ServicePointSearchResult) {
    if (searchResultSelected) {
      this.router
        .navigate([Pages.SERVICE_POINTS.path, searchResultSelected.number], {
          relativeTo: this.route,
        })
        .then();
    } else {
      this.servicePointSearchResult$ = of([]);
    }
  }

  searchServicePoint(value: string): void {
    if (value) {
      this._searchValue = value.trim();
      if (!this._searchValue) {
        return;
      }
      this.servicePointSearchResult$ = this.servicePointService.searchServicePoints(
        this._searchValue
      );
    }
  }
}
