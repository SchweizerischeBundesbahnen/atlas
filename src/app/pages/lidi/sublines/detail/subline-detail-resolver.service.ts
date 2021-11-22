import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api';
import { Pages } from '../../../pages';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class SublineDetailResolver implements Resolve<Partial<SublineVersion>> {
  constructor(private sublinesService: SublinesService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Partial<SublineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of({})
      : this.sublinesService.getSubline(idParameter).pipe(
          catchError(() => {
            this.router.navigate([Pages.LIDI.path]).then();
            return EMPTY;
          }),
          map((arr) => arr[0]) // TODO: remove once detail supports multiple
        );
  }
}
