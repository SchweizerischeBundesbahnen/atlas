import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { ReadSublineVersionV2, SublineVersion ,
} from '../../../../api';
import { Pages } from '../../../pages';
import { SublineService } from '../../../../api/service/lidi/subline.service';

@Injectable({ providedIn: 'root' })
export class SublineDetailResolver {
  constructor(
    private readonly sublinesService: SublineService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadSublineVersionV2>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.sublinesService.getSublineVersionV2(idParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.LIDI.path, Pages.SUBLINES.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => [])
          )
        );
  }
}

export const sublineResolver: ResolveFn<Array<SublineVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(SublineDetailResolver).resolve(route);
