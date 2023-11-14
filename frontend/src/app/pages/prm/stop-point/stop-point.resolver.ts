import {ActivatedRouteSnapshot, ResolveFn, Router} from '@angular/router';
import {inject, Injectable} from "@angular/core";
import {PersonWithReducedMobilityService, ReadStopPointVersion} from "../../../api";
import {catchError, Observable, of} from "rxjs";
import {Pages} from "../../pages";


@Injectable({ providedIn: 'root' })
export class StopPointResolver {
  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<ReadStopPointVersion>> {
    const sloidParameter = route.paramMap.get('sloid') || '';
    return sloidParameter === 'add'
      ? of([])
      : this.personWithReducedMobilityService.getStopPointVersions(sloidParameter).pipe(
        catchError(() =>
          this.router
            .navigate([Pages.PRM.path], {
              state: { notDismissSnackBar: true }
            })
            .then(() => [])
        )
      );
  }
}
export const stopPointResolver: ResolveFn<Array<ReadStopPointVersion>> = (
  route: ActivatedRouteSnapshot
) => inject(StopPointResolver).resolve(route);
