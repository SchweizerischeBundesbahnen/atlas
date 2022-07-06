import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, forkJoin, Observable } from 'rxjs';
import {
  TransportCompaniesService,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyRelationsService,
} from '../../../../api';
import { Pages } from '../../../pages';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TranslateService } from '@ngx-translate/core';
import { Language } from '../../../../core/components/language-switcher/language';

@Injectable({ providedIn: 'root' })
export class TransportCompanyDetailResolver
  implements Resolve<[TransportCompany, TransportCompanyBoRelation[]]>
{
  constructor(
    private readonly transportCompaniesService: TransportCompaniesService,
    private notificationService: NotificationService,
    private readonly router: Router,
    private readonly transportCompanyRelationsService: TransportCompanyRelationsService,
    private readonly translateService: TranslateService
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<[TransportCompany, TransportCompanyBoRelation[]]> {
    const idParameter = parseInt(route.paramMap.get('id') || '0');
    if (Number.isNaN(idParameter)) {
      this.notificationService.error(new Error(), 'BODI.TRANSPORT_COMPANIES.ID_NAN_ERROR');
      return this.routeOnFailure();
    }
    return forkJoin(
      this.transportCompaniesService.getTransportCompany(idParameter).pipe(
        catchError(() => {
          return this.routeOnFailure();
        })
      ),
      this.transportCompanyRelationsService
        .getTransportCompanyRelations(idParameter, this.translateService.currentLang ?? Language.DE)
        .pipe(catchError(() => this.routeOnFailure()))
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
