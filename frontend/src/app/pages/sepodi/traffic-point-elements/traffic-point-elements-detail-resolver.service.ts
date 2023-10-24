import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { ReadTrafficPointElementVersionModel, TrafficPointElementsService } from '../../../api';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class TrafficPointElementsDetailResolver {
  constructor(
    private readonly trafficPointElementsService: TrafficPointElementsService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadTrafficPointElementVersionModel>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.trafficPointElementsService.getTrafficPointElement(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.SEPODI.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => []),
          ),
        );
  }
}

export const trafficPointResolver: ResolveFn<Array<ReadTrafficPointElementVersionModel>> = (
  route: ActivatedRouteSnapshot,
) => inject(TrafficPointElementsDetailResolver).resolve(route);
