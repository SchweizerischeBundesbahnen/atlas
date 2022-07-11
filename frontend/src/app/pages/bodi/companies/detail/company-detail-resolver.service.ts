import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { CompaniesService, Company } from '../../../../api';
import { Pages } from '../../../pages';
import { NotificationService } from '../../../../core/notification/notification.service';

@Injectable({ providedIn: 'root' })
export class CompanyDetailResolver implements Resolve<Company> {
  constructor(
    private readonly companiesService: CompaniesService,
    private notificationService: NotificationService,
    private readonly router: Router
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Company> {
    const idParameter = parseInt(route.paramMap.get('id') || '0');
    if (Number.isNaN(idParameter)) {
      this.notificationService.error(new Error(), 'BODI.TRANSPORT_COMPANIES.ID_NAN_ERROR');
      return this.routeOnFailure();
    }
    return this.companiesService.getCompany(idParameter).pipe(
      catchError(() => {
        return this.routeOnFailure();
      })
    );
  }

  routeOnFailure() {
    this.router
      .navigate([Pages.BODI.path, Pages.COMPANIES.path], {
        state: { notDismissSnackBar: true },
      })
      .then();
    return EMPTY;
  }
}
