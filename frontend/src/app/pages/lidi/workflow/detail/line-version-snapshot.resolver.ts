import { inject, Injectable } from '@angular/core';
import { LinesService, LineVersionSnapshot } from '../../../../api';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable } from 'rxjs';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class LineVersionSnapshotResolver {
  constructor(
    private readonly linesService: LinesService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<LineVersionSnapshot | never[]> {
    const idParameter = route.params.id;
    return this.linesService.getLineVersionSnapshotById(Number(idParameter)).pipe(
      catchError(() =>
        this.router
          .navigate([Pages.LIDI.path, Pages.WORKFLOWS.path], {
            state: { notDismissSnackBar: true },
          })
          .then(() => []),
      ),
    );
  }
}

export const lineVersionSnapshotResolver: ResolveFn<LineVersionSnapshot | never[]> = (
  route: ActivatedRouteSnapshot,
) => inject(LineVersionSnapshotResolver).resolve(route);
