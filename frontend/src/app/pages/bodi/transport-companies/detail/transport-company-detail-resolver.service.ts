import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, EMPTY, forkJoin, Observable } from 'rxjs';
import { TransportCompany, TransportCompanyBoRelation } from '../../../../api';
import { Pages } from '../../../pages';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TransportCompanyInternalService } from '../../../../api/service/bodi/transport-company-internal.service';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';

@Injectable({ providedIn: 'root' })
export class TransportCompanyDetailResolver {
  constructor(
    private readonly transportCompanyInternalService: TransportCompanyInternalService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly transportCompanyRelationInternalService: TransportCompanyRelationInternalService
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<[TransportCompany, TransportCompanyBoRelation[]]> {
    const idParameter = parseInt(route.paramMap.get('id') || '0');
    if (Number.isNaN(idParameter)) {
      this.notificationService.error(
        new Error(),
        'BODI.TRANSPORT_COMPANIES.ID_NAN_ERROR'
      );
      return this.routeOnFailure();
    }
    return forkJoin([
      this.transportCompanyInternalService
        .getTransportCompany(idParameter)
        .pipe(
          catchError(() => {
            return this.routeOnFailure();
          })
        ),
      this.transportCompanyRelationInternalService
        .getTransportCompanyRelations(idParameter)
        .pipe(catchError(() => this.routeOnFailure())),
    ]);
  }

  routeOnFailure() {
    this.router
      .navigate([Pages.BODI.path, Pages.TRANSPORT_COMPANIES.path], {
        state: { notDismissSnackBar: true },
      })
      .then();
    return EMPTY;
  }
}

export const transportCompanyResolver: ResolveFn<
  [TransportCompany, TransportCompanyBoRelation[]]
> = (route: ActivatedRouteSnapshot) =>
  inject(TransportCompanyDetailResolver).resolve(route);
