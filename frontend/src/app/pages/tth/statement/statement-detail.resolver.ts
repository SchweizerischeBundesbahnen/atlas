import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { TimetableHearingService, TimetableHearingStatement } from '../../../api';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class StatementDetailResolver implements Resolve<TimetableHearingStatement | undefined> {
  constructor(
    private readonly timetableHearingService: TimetableHearingService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<TimetableHearingStatement | undefined> {
    const idParameter = route.paramMap.get('id') || '0';
    return idParameter === 'add'
      ? of(undefined)
      : this.timetableHearingService.getStatement(parseInt(idParameter)).pipe(
          catchError(() => {
            this.router
              .navigate(
                [
                  Pages.TTH.path,
                  route.paramMap.get('canton')?.toLowerCase(),
                  Pages.TTH_ACTIVE.path,
                ],
                {
                  state: { notDismissSnackBar: true },
                }
              )
              .then();
            return of(undefined);
          })
        );
  }
}
