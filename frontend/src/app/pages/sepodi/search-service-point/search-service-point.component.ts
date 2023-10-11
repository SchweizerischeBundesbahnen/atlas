import { Component, Input, OnInit } from '@angular/core';
import { ServicePointSearchResult, ServicePointsService } from '../../../api';
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
import { Pages } from '../../pages';
import { filter, switchMap, tap } from 'rxjs/operators';
import { TranslatePipe } from '@ngx-translate/core';

class ServicePointSearchType {
  public static readonly servicePoint = 'SERVICE_POINT';
  public static readonly servicePointBps = 'SERVICE_POINT_BPS';
  // 'SERVICE_POINT': string;
  // 'SERVICE_POINT_BPS': string;
}

@Component({
  selector: 'app-search-service-point',
  templateUrl: './search-service-point.component.html',
  styleUrls: ['./search-service-point.component.scss'],
})
export class SearchServicePointComponent implements OnInit {
  private readonly MIN_LENGTH_TERM = 2;
  private readonly _DEBOUNCE_TIME = 500;

  @Input() firstLabel = 'SEPODI.SERVICE_POINTS.DIDOK_CODE_SEARCH';
  @Input() secondLabel = 'SEPODI.SERVICE_POINTS.DESIGNATION_OFFICIAL';
  @Input() placeholder = 'SEPODI.SERVICE_POINTS.SERVICE_POINT';
  @Input() searchAllServicePoints = true;
  // @Input() searchType = 'SERVICE_POINT';
  @Input() searchType = ServicePointSearchType.servicePoint;
  @Input() editable = true;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly servicePointService: ServicePointsService,
    private readonly translatePipe: TranslatePipe,
  ) {}

  private _searchValue = '';

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

  private getNotFoundTranslatedLabel() {
    return this.translatePipe.transform('COMMON.NODATAFOUND');
  }

  private getTypeToSearchTranslatedLabel() {
    return this.translatePipe.transform('COMMON.TYPE_TO_SEARCH_SHORT');
  }

  servicePointSearchResult$: Observable<ServicePointSearchResult[]> = of([]);
  searchInput$ = new Subject<string>();
  loading = false;

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
          if (this.searchAllServicePoints) {
            return this.servicePointService.searchServicePoints({ value: term }).pipe(
              catchError(() => of([])),
              tap(() => (this.loading = false)),
            );
          } else {
            return this.servicePointService.searchServicePoints({ value: term }).pipe(
              catchError(() => of([])),
              tap(() => (this.loading = false)),
            );
          }
        }),
      ),
    );
  }

  initSearchValue(searchValue: string) {
    this._searchValue = searchValue == null ? '' : searchValue.trim();
  }

  clearResult() {
    this._searchValue = '';
    this.loadResult();
  }
}
