import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { ReadTrafficPointElementVersion, TrafficPointElementsService } from '../../../../api';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class TrafficPointElementResolver {
  constructor(
    private readonly trafficPointElementsService: TrafficPointElementsService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadTrafficPointElementVersion>> {
    const sloidParameter = route.paramMap.get('platformSloid') || '';
    return this.trafficPointElementsService.getTrafficPointElement(sloidParameter).pipe(
      catchError(() =>
        this.router
          .navigate([Pages.PRM.path], {
            state: { notDismissSnackBar: true },
          })
          .then(() => []),
      ),
    );
  }
}

export const trafficPointElementResolver: ResolveFn<Array<ReadTrafficPointElementVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(TrafficPointElementResolver).resolve(route);
