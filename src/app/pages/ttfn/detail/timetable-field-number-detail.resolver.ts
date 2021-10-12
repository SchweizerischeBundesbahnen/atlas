import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { TimetableFieldNumbersService, Version } from '../../../api/ttfn';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<Version> {
  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Version> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? EMPTY
      : this.timetableFieldNumbersService.getVersion(parseInt(idParameter)).pipe(
          catchError(() => {
            this.router.navigate(['']).then();
            return EMPTY;
          })
        );
  }
}
