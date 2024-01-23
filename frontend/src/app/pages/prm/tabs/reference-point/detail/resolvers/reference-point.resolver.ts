import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import {
  PersonWithReducedMobilityService,
  ReadPlatformVersion,
  ReadReferencePointVersion,
} from '../../../../../../api';
import { Pages } from '../../../../../pages';

@Injectable({ providedIn: 'root' })
export class PrmReferencePointResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadReferencePointVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return this.personWithReducedMobilityService.getReferencePointVersions(sloidParameter).pipe(
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

export const referencePointResolver: ResolveFn<Array<ReadPlatformVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(PrmReferencePointResolver).resolve(route);
