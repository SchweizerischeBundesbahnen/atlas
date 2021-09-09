import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { TimetableFieldNumbersService, Version } from '../../api';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<Version> {
  newVersion: Version = {
    ttfnid: 'ttfnid',
    name: 'name',
    swissTimetableFieldNumber: 'asdf',
    status: 'ACTIVE',
    validFrom: new Date(),
    validTo: new Date('2029-12-31'),
  };

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Version> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of(this.newVersion)
      : this.timetableFieldNumbersService.getVersion(parseInt(idParameter)).pipe(
          catchError(() => {
            this.router.navigate(['']).then();
            return EMPTY;
          })
        );
  }
}
