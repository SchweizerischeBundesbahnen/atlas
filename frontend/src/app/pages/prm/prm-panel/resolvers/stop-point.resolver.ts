import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { ReadStopPointVersion } from '../../../../api';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { StopPointService } from '../../../../api/service/prm/stop-point/stop-point.service';

@Injectable({ providedIn: 'root' })
export class StopPointResolver {
  constructor(
    private readonly stopPointService: StopPointService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadStopPointVersion>> {
    const sloidParameter = route.paramMap.get('stopPointSloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.stopPointService.getStopPointVersions(sloidParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.PRM.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => [])
          )
        );
  }
}

export const stopPointResolver: ResolveFn<Array<ReadStopPointVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(StopPointResolver).resolve(route);
