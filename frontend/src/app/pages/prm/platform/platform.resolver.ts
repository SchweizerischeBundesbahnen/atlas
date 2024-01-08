import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { PersonWithReducedMobilityService, ReadPlatformVersion } from '../../../api';
import { Pages } from '../../pages';

@Injectable({ providedIn: 'root' })
export class PrmPlatformResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadPlatformVersion>> {
    const sloidParameter = route.paramMap.get('platformSloid') || '';
    return this.personWithReducedMobilityService.getPlatformVersions(sloidParameter).pipe(
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

export const platformResolver: ResolveFn<Array<ReadPlatformVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(PrmPlatformResolver).resolve(route);
