import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { ReadServicePointVersion, ServicePointsService } from '../../../api';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class ServicePointDetailResolver {
  constructor(
    private readonly servicePointsService: ServicePointsService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadServicePointVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.servicePointsService
          .getServicePointVersions(Number(idParameter))
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

export const servicePointResolver: ResolveFn<Array<ReadServicePointVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(ServicePointDetailResolver).resolve(route);
