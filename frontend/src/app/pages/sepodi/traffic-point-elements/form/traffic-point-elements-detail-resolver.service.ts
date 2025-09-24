import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, Observable, of } from 'rxjs';
import {
  ReadTrafficPointElementVersion,
  TrafficPointElementType,
} from '../../../../api';
import { Pages } from '../../../pages';
import { TrafficPointElementService } from '../../../../api/service/sepodi/traffic-point-element.service';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class TrafficPointElementsDetailResolver {
  constructor(
    private readonly trafficPointElementService: TrafficPointElementService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<Array<ReadTrafficPointElementVersion>> {
    const trafficPointSloid = route.paramMap.get('trafficPointSloid') ?? '';
    return trafficPointSloid === 'add'
      ? of([])
      : this.trafficPointElementService
          .getTrafficPointElement(trafficPointSloid)
          .pipe(
            tap((trafficPoint) =>
              this.checkRouteTypeMatch(trafficPoint, route)
            ),
            catchError(() =>
              this.router
                .navigate([Pages.SEPODI.path], {
                  state: { notDismissSnackBar: true },
                })
                .then(() => [])
            )
          );
  }

  private checkRouteTypeMatch(
    trafficPoint: ReadTrafficPointElementVersion[],
    route: ActivatedRouteSnapshot
  ) {
    const trafficPointExists = trafficPoint && trafficPoint.length > 0;
    if (trafficPointExists) {
      const firstVersion = trafficPoint[0];
      const existingVersionIsArea =
        firstVersion.trafficPointElementType ===
        TrafficPointElementType.BoardingArea;
      if ((route.data.isTrafficPointArea ?? false) != existingVersionIsArea) {
        const redirectPath = existingVersionIsArea
          ? Pages.TRAFFIC_POINT_ELEMENTS_AREA.path
          : Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path;
        this.router
          .navigate([
            Pages.SEPODI.path,
            Pages.SERVICE_POINTS.path,
            route.paramMap.get('servicePointNumber'),
            redirectPath,
            firstVersion.sloid,
          ])
          .then();
      }
    }
  }
}

export const trafficPointResolver: ResolveFn<
  Array<ReadTrafficPointElementVersion>
> = (route: ActivatedRouteSnapshot) =>
  inject(TrafficPointElementsDetailResolver).resolve(route);
