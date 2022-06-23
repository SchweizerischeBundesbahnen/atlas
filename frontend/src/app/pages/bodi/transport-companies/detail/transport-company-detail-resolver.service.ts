import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { TransportCompaniesService, TransportCompany } from '../../../../api';
import { Pages } from '../../../pages';
import { NotificationService } from '../../../../core/notification/notification.service';

@Injectable({ providedIn: 'root' })
export class TransportCompanyDetailResolver implements Resolve<TransportCompany> {
  constructor(
    private readonly transportCompaniesService: TransportCompaniesService,
    private notificationService: NotificationService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<TransportCompany> {
    const idParameter = parseInt(route.paramMap.get('id') || '0');
    if (Number.isNaN(idParameter)) {
      this.notificationService.error(new Error(), 'BODI.TRANSPORT_COMPANIES.ID_NAN_ERROR');
      return this.routeOnFailure();
    }
    return this.transportCompaniesService.getTransportCompany(idParameter).pipe(
      catchError(() => {
        return this.routeOnFailure();
      })
    );
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
