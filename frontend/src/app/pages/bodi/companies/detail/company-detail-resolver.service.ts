import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { Company } from '../../../../api';
import { Pages } from '../../../pages';
import { CompanyService } from '../../../../api/service/bodi/company.service';

@Injectable({ providedIn: 'root' })
export class CompanyDetailResolver {
  private readonly companyInternalService = inject(CompanyService);
  private readonly router = inject(Router);

  resolve(route: ActivatedRouteSnapshot): Observable<Company> {
    const idParameter = route.paramMap.get('id')!;
    return this.companyInternalService.getCompany(idParameter).pipe(
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

export const companyResolver: ResolveFn<Company> = (route: ActivatedRouteSnapshot) =>
  inject(CompanyDetailResolver).resolve(route);
