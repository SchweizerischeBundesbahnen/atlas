import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { TimetableFieldNumbersService, TimetableFieldNumberVersion } from '../../../api';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver {
  constructor(
    private readonly timetableFieldNumbersService: TimetableFieldNumbersService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<TimetableFieldNumberVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.timetableFieldNumbersService.getAllVersionsVersioned(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.TTFN.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => []),
          ),
        );
  }
}

export const timetableFieldNumberResolver: ResolveFn<Array<TimetableFieldNumberVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(TimetableFieldNumberDetailResolver).resolve(route);
