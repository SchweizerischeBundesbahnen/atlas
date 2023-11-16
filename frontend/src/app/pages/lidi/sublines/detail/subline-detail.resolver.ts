import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class SublineDetailResolver {
  constructor(
    private readonly sublinesService: SublinesService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<SublineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.sublinesService.getSublineVersion(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.LIDI.path, Pages.SUBLINES.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => []),
          ),
        );
  }
}

export const sublineResolver: ResolveFn<Array<SublineVersion>> = (route: ActivatedRouteSnapshot) =>
  inject(SublineDetailResolver).resolve(route);
