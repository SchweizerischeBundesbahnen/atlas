import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { ReadParkingLotVersion } from '../../../../../../api';
import { Pages } from '../../../../../pages';
import { ParkingLotService } from '../../../../../../api/service/prm/parking-lot/parking-lot.service';

@Injectable({ providedIn: 'root' })
export class PrmParkingLotResolver {
  constructor(
    private readonly parkingLotService: ParkingLotService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadParkingLotVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.parkingLotService.getParkingLotVersions(sloidParameter).pipe(
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

export const parkingLotResolver: ResolveFn<Array<ReadParkingLotVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(PrmParkingLotResolver).resolve(route);
