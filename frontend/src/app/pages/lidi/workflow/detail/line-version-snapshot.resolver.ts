import { inject, Injectable } from '@angular/core';
import { LineVersionSnapshot } from '../../../../api';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable } from 'rxjs';
import { Pages } from '../../../pages';
import { LineInternalService } from '../../../../api/service/lidi/line-internal.service';

@Injectable({ providedIn: 'root' })
export class LineVersionSnapshotResolver {
  constructor(
    private readonly lineInternalService: LineInternalService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<LineVersionSnapshot | never[]> {
    const idParameter = route.params.id;
    return this.lineInternalService
      .getLineVersionSnapshotById(Number(idParameter))
      .pipe(
        catchError(() =>
          this.router
            .navigate([Pages.LIDI.path, Pages.WORKFLOWS.path], {
              state: { notDismissSnackBar: true },
            })
            .then(() => [])
        )
      );
  }
}

export const lineVersionSnapshotResolver: ResolveFn<
  LineVersionSnapshot | never[]
> = (route: ActivatedRouteSnapshot) =>
  inject(LineVersionSnapshotResolver).resolve(route);
