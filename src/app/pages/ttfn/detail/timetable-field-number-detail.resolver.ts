import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { TimetableFieldNumbersService, Version } from '../../../api';
import { Pages } from '../../pages';
import { NotificationService } from '../../../core/notification/notification.service';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<Array<Version>> {
  resolve(route: ActivatedRouteSnapshot): Observable<Array<Version>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.timetableFieldNumbersService.getAllVersionsVersioned(idParameter).pipe(
          catchError((error) => {
            this.router.navigate([Pages.TTFN.path]).then();
            this.notificationService.error(error);
            return EMPTY;
          })
        );
  }

  constructor(
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private notificationService: NotificationService,
    private router: Router
  ) {}
}
