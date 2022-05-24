import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { LinesService, LineVersion } from '../../../../api';
import { NotificationService } from '../../../../core/notification/notification.service';
import { Pages } from '../../../pages';

@Injectable({ providedIn: 'root' })
export class BusinessOrganisationDetailResolver implements Resolve<Array<LineVersion>> {
  constructor(
    private linesService: LinesService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<LineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.linesService.getLineVersions(idParameter).pipe(
          catchError((error) => {
            this.router.navigate([Pages.LIDI.path]).then();
            this.notificationService.error(error);
            return EMPTY;
          })
        );
  }
}
