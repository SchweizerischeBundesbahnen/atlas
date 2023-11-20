import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { LinesService, LineVersion } from '../../../../api';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class LineDetailResolver {
  constructor(
    private readonly linesService: LinesService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<LineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.linesService.getLineVersions(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.LIDI.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => []),
          ),
        );
  }
}

export const lineResolver: ResolveFn<Array<LineVersion>> = (route: ActivatedRouteSnapshot) =>
  inject(LineDetailResolver).resolve(route);
