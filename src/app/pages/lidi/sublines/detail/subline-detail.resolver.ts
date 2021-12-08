import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class SublineDetailResolver implements Resolve<Array<SublineVersion>> {
  constructor(private sublinesService: SublinesService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<SublineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.sublinesService.getSublineVersion(idParameter).pipe(
          catchError(() => {
            this.router.navigate([Pages.LIDI.path]).then();
            return EMPTY;
          })
        );
  }
}
