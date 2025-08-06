import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { ReadLoadingPointVersion } from '../../../api';
import { Pages } from '../../pages';
import { LoadingPointService } from '../../../api/service/sepodi/loading-point.service';

@Injectable({ providedIn: 'root' })
export class LoadingPointsDetailResolver {
  constructor(
    private readonly loadingPointsService: LoadingPointService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadLoadingPointVersion>> {
    const servicePointNumber = route.paramMap.get('servicePointNumber') || '';
    const number = route.paramMap.get('number') || '';
    return number === 'add'
      ? of([])
      : this.loadingPointsService
          .getLoadingPoint(Number(servicePointNumber), Number(number))
          .pipe(
            catchError(() =>
              this.router
                .navigate([Pages.SEPODI.path], {
                  state: { notDismissSnackBar: true },
                })
                .then(() => [])
            )
          );
  }
}

export const loadingPointResolver: ResolveFn<Array<ReadLoadingPointVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(LoadingPointsDetailResolver).resolve(route);
