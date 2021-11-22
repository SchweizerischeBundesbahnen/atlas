import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { LinesService, LineVersion } from '../../../../api';
import { Pages } from '../../../pages';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class LineDetailResolver implements Resolve<Partial<LineVersion>> {
  constructor(private linesService: LinesService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Partial<LineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of({})
      : this.linesService.getLine(idParameter).pipe(
          catchError(() => {
            this.router.navigate([Pages.LIDI.path]).then();
            return EMPTY;
          }),
          map((arr) => arr[0]) // TODO: remove once detail supports multiple
        );
  }
}
