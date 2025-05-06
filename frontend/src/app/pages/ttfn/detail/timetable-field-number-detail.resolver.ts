import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import {
  TimetableFieldNumberVersion,
} from '../../../api';
import { Pages } from '../../pages';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver {
  constructor(
    private readonly timetableFieldNumbersService: TimetableFieldNumberService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<TimetableFieldNumberVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.timetableFieldNumbersService
          .getAllVersionsVersioned(idParameter)
          .pipe(
            catchError(() =>
              this.router
                .navigate([Pages.TTFN.path], {
                  state: { notDismissSnackBar: true },
                })
                .then(() => [])
            )
          );
  }
}

export const timetableFieldNumberResolver: ResolveFn<
  Array<TimetableFieldNumberVersion>
> = (route: ActivatedRouteSnapshot) =>
  inject(TimetableFieldNumberDetailResolver).resolve(route);
