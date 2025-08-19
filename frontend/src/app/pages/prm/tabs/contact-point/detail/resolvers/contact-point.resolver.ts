import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { ReadContactPointVersion } from '../../../../../../api';
import { Pages } from '../../../../../pages';
import { ContactPointService } from '../../../../../../api/service/prm/contact-point/contact-point.service';

@Injectable({ providedIn: 'root' })
export class PrmContactPointResolver {
  constructor(
    private readonly contactPointService: ContactPointService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadContactPointVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.contactPointService.getContactPointVersions(sloidParameter).pipe(
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

export const contactPointResolver: ResolveFn<Array<ReadContactPointVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(PrmContactPointResolver).resolve(route);
