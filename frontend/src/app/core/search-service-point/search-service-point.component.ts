import { Component, Input, OnInit, Output } from '@angular/core';
import { ServicePointSearchResult, ServicePointsService } from '../../api';
import {
  catchError,
  concat,
  debounceTime,
  distinctUntilChanged,
  Observable,
  of,
  Subject,
} from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, switchMap, tap } from 'rxjs/operators';
import { TranslatePipe } from '@ngx-translate/core';
import { ServicePointSearch, ServicePointSearchType } from './service-point-search';

const SEARCH_SERVICE_POINT_PLACEHOLDER = 'SEPODI.SERVICE_POINTS.SERVICE_POINT';
const SEARCH_STOP_POINT_PLACEHOLDER = 'SEPODI.SERVICE_POINTS.STOP_POINT';

@Component({
  selector: 'app-search-service-point',
  templateUrl: './search-service-point.component.html',
  styleUrls: ['./search-service-point.component.scss'],
})
export class SearchServicePointComponent implements OnInit {
  private readonly MIN_LENGTH_TERM = 2;
  private readonly _DEBOUNCE_TIME = 500;

  @Input() searchType!: ServicePointSearchType;
  @Output()
  private _searchValue = '';
  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);
  searchInput$ = new Subject<string>();
  loading = false;

  get searchPlaceholder() {
    return this.searchType === ServicePointSearch.SePoDi
      ? SEARCH_SERVICE_POINT_PLACEHOLDER
      : SEARCH_STOP_POINT_PLACEHOLDER;
  }

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly servicePointService: ServicePointsService,
    private readonly translatePipe: TranslatePipe,
  ) {}

  get searchValue(): string {
    return this._searchValue;
  }

  get minThermLongText(): string {
    if (!this._searchValue || this._searchValue.length < this.MIN_LENGTH_TERM) {
      return this.getTypeToSearchTranslatedLabel();
    }
    return this.getNotFoundTranslatedLabel();
  }

  get notFoundText(): string {
    if (!this._searchValue || this._searchValue.length >= this.MIN_LENGTH_TERM) {
      return this.getNotFoundTranslatedLabel();
    }
    return this.getTypeToSearchTranslatedLabel();
  }

  ngOnInit(): void {
    this.loadResult();
  }

  loadResult() {
    this.servicePointSearchResult$ = concat(
      of([]),
      this.searchInput$.pipe(
        filter((res) => {
          return res !== null && res.length >= 0;
        }),
        distinctUntilChanged(),
        debounceTime(this._DEBOUNCE_TIME),
        tap((searchValue) => {
          this.initSearchValue(searchValue);
          this.loading = true;
        }),
        switchMap((term) => {
          if (term.length < this.MIN_LENGTH_TERM) {
            return of([]).pipe(tap(() => (this.loading = false)));
          }
          return this.search(term);
        }),
      ),
    );
  }

  clearResult() {
    this._searchValue = '';
    this.loadResult();
  }

  navigateTo(searchResultSelected: ServicePointSearchResult) {
    if (searchResultSelected) {
      this.navigate(searchResultSelected);
    } else {
      this.servicePointSearchResult$ = of([]);
    }
  }

  private search(term: string) {
    if (this.searchType === ServicePointSearch.SePoDi) {
      return this.searchServicePoint(term);
    }
    return this.searchSwissOnlyServicePointAsStopPoint(term);
  }

  private searchSwissOnlyServicePointAsStopPoint(term: string) {
    return this.servicePointService.searchSwissOnlyServicePoints({ value: term }).pipe(
      catchError(() => of([])),
      tap(() => (this.loading = false)),
    );
  }

  private searchServicePoint(term: string) {
    return this.servicePointService.searchServicePoints({ value: term }).pipe(
      catchError(() => of([])),
      tap(() => (this.loading = false)),
    );
  }

  initSearchValue(searchValue: string) {
    this._searchValue = searchValue == null ? '' : searchValue.trim();
  }

  private navigate(searchResultSelected: ServicePointSearchResult) {
    this.router
      .navigate(
        [
          this.searchType.navigationPath,
          this.searchType === ServicePointSearch.SePoDi
            ? searchResultSelected.number
            : searchResultSelected.sloid,
        ],
        { relativeTo: this.route },
      )
      .then();
  }

  private getNotFoundTranslatedLabel() {
    return this.translatePipe.transform('COMMON.NODATAFOUND');
  }

  private getTypeToSearchTranslatedLabel() {
    return this.translatePipe.transform('COMMON.TYPE_TO_SEARCH_SHORT');
  }
}
