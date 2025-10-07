import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { SectorGroupVersion } from '../../../../api/model/sectorGroupVersion';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';

@Injectable({ providedIn: 'root' })
export class SectorGroupDetailResolver {
  private readonly sectorGroupService = inject(SectorGroupService);
  private readonly router = inject(Router);

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<SectorGroupVersion>> {
    const sectorGroupSloid = route.paramMap.get('sectorGroupSloid') ?? '';
    return sectorGroupSloid === 'add'
      ? of([])
      : this.sectorGroupService.getSectorGroup(sectorGroupSloid).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.SEPODI.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => [])
          )
        );
  }
}

export const sectorGroupResolver: ResolveFn<Array<SectorGroupVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(SectorGroupDetailResolver).resolve(route);
