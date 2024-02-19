import {ActivatedRouteSnapshot, ResolveFn, Router} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {catchError, Observable, of} from 'rxjs';
import {PersonWithReducedMobilityService, ReadContactPointVersion,} from '../../../../../../api';
import {Pages} from '../../../../../pages';

@Injectable({ providedIn: 'root' })
export class PrmContactPointResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadContactPointVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.personWithReducedMobilityService.getContactPointVersions(sloidParameter).pipe(
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

export const contactPointResolver: ResolveFn<Array<ReadContactPointVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(PrmContactPointResolver).resolve(route);
