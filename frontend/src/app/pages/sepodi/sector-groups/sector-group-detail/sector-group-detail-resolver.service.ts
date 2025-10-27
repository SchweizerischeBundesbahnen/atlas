import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';
import { ReadSectorGroupVersion } from '../../../../api/model/readSectorGroupVersion';

@Injectable({ providedIn: 'root' })
export class SectorGroupDetailResolver {
  private readonly sectorGroupService = inject(SectorGroupService);
  private readonly router = inject(Router);

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadSectorGroupVersion>> {
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

export const sectorGroupResolver: ResolveFn<Array<ReadSectorGroupVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(SectorGroupDetailResolver).resolve(route);
