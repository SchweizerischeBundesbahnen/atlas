import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { ReadReferencePointVersion } from '../../../../../../api';
import { Pages } from '../../../../../pages';
import { ReferencePointService } from '../../../../../../api/service/prm/reference-point/reference-point.service';

@Injectable({ providedIn: 'root' })
export class PrmReferencePointResolver {
  constructor(
    private readonly referencePointService: ReferencePointService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadReferencePointVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.referencePointService
          .getReferencePointVersions(sloidParameter)
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

export const referencePointResolver: ResolveFn<
  Array<ReadReferencePointVersion>
> = (route: ActivatedRouteSnapshot) =>
  inject(PrmReferencePointResolver).resolve(route);
