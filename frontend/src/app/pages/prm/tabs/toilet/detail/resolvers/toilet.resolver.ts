import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { ToiletVersion } from '../../../../../../api';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../../../pages';
import { ToiletService } from '../../../../../../api/service/prm/toilet/toilet.service';

@Injectable({ providedIn: 'root' })
export class ToiletResolver {
  private readonly toiletService = inject(ToiletService);
  private readonly router = inject(Router);

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ToiletVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.toiletService.getToiletVersions(sloidParameter).pipe(
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

export const toiletResolver: ResolveFn<Array<ToiletVersion>> = (route: ActivatedRouteSnapshot) =>
  inject(ToiletResolver).resolve(route);
