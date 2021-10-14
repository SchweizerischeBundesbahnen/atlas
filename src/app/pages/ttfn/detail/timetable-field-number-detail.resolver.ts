import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { TimetableFieldNumbersService, Version } from '../../../api/ttfn';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<Version> {
  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Version> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of({})
      : this.timetableFieldNumbersService.getVersion(parseInt(idParameter)).pipe(
          catchError(() => {
            this.router.navigate([Pages.TTFN.path]).then();
            return EMPTY;
          })
        );
  }
}
