import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { TimetableHearingStatement, TimetableHearingStatementsService } from '../../../api';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class StatementDetailResolver {
  constructor(
    private readonly timetableHearingStatementsService: TimetableHearingStatementsService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<TimetableHearingStatement | undefined> {
    const idParameter = route.paramMap.get('id') || '0';
    const hearingStatus = route.data['hearingStatus'];
    return idParameter === 'add'
      ? of(undefined)
      : this.timetableHearingStatementsService.getStatement(parseInt(idParameter)).pipe(
          catchError(() => {
            this.router
              .navigate(
                [
                  Pages.TTH.path,
                  route.paramMap.get('canton')?.toLowerCase(),
                  hearingStatus.toLowerCase(),
                ],
                {
                  state: { notDismissSnackBar: true },
                },
              )
              .then();
            return of(undefined);
          }),
        );
  }
}

export const statementResolver: ResolveFn<TimetableHearingStatement | undefined> = (
  route: ActivatedRouteSnapshot,
) => inject(StatementDetailResolver).resolve(route);
