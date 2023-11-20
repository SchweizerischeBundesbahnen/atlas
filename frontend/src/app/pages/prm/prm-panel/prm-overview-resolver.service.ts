import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import {
  BusinessOrganisationsService,
  PersonWithReducedMobilityService,
  ReadServicePointVersion,
  ServicePointsService,
} from '../../../api';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class PrmOverviewResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly servicePointsService: ServicePointsService,
    private businessOrganisationsService: BusinessOrganisationsService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadServicePointVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';

    return sloidParameter === 'add'
      ? of([])
      : this.servicePointsService.getServicePointVersionsBySloid(sloidParameter).pipe(
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
export const prmOverviewResolver: ResolveFn<Array<ReadServicePointVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(PrmOverviewResolver).resolve(route);
