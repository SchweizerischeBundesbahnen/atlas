import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import {
  PersonWithReducedMobilityService,
  ToiletVersion,
} from '../../../../../../api';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../../../pages';

@Injectable({ providedIn: 'root' })
export class ToiletResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ToiletVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.personWithReducedMobilityService
          .getToiletVersions(sloidParameter)
          .pipe(
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

export const toiletResolver: ResolveFn<Array<ToiletVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(ToiletResolver).resolve(route);
