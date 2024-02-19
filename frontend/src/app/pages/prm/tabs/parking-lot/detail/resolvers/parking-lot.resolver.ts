import {ActivatedRouteSnapshot, ResolveFn, Router} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {catchError, Observable, of} from 'rxjs';
import {PersonWithReducedMobilityService, ReadParkingLotVersion,} from '../../../../../../api';
import {Pages} from '../../../../../pages';

@Injectable({ providedIn: 'root' })
export class PrmParkingLotResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadParkingLotVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.personWithReducedMobilityService.getParkingLotVersions(sloidParameter).pipe(
          catchError(() =>
            this.router
              .navigate([Pages.PRM.path], {
                state: { notDismissSnackBar: true },
              })
              .then(() => []),
          ),
        );
  }
}

export const parkingLotResolver: ResolveFn<Array<ReadParkingLotVersion>> = (
  route: ActivatedRouteSnapshot,
) => inject(PrmParkingLotResolver).resolve(route);
