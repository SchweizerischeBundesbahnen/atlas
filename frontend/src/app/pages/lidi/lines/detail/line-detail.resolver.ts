import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { LineVersionV2 } from '../../../../api';
import { Pages } from '../../../pages';
import { LineService } from '../../../../api/service/line.service';

@Injectable({ providedIn: 'root' })
export class LineDetailResolver {
  constructor(
    private readonly lineService: LineService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<LineVersionV2>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.lineService.getLineVersionsV2(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.LIDI.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => [])
          )
        );
  }
}

export const lineResolver: ResolveFn<Array<LineVersionV2>> = (
  route: ActivatedRouteSnapshot
) => inject(LineDetailResolver).resolve(route);
