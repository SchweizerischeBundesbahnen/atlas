import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { ReadServicePointVersion } from '../../../api';
import { Pages } from '../../pages';
import { ServicePointService } from '../../../api/service/sepodi/service-point.service';

@Injectable({ providedIn: 'root' })
export class ServicePointDetailResolver {
  private readonly servicePointService = inject(ServicePointService);
  private readonly router = inject(Router);

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadServicePointVersion>> {
    const idParameter = route.paramMap.get('servicePointNumber') ?? '';
    return idParameter === 'add'
      ? of([])
      : this.servicePointService.getServicePointVersions(Number(idParameter)).pipe(
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

export const servicePointResolver: ResolveFn<Array<ReadServicePointVersion>> = (route: ActivatedRouteSnapshot) =>
  inject(ServicePointDetailResolver).resolve(route);
