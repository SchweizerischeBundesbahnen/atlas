import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { SectorService } from '../../../../api/service/sepodi/sector.service';
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';

@Injectable({ providedIn: 'root' })
export class SectorDetailResolver {
  private readonly sectorService = inject(SectorService);
  private readonly router = inject(Router);

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadSectorVersion>> {
    const sectorSloid = route.paramMap.get('sectorSloid') ?? '';
    return sectorSloid === 'add'
      ? of([])
      : this.sectorService.getSector(sectorSloid).pipe(
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

export const sectorResolver: ResolveFn<Array<ReadSectorVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(SectorDetailResolver).resolve(route);
