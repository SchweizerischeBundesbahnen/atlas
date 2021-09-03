import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TimetableFieldNumbersService } from '../api';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<any> {
  newVersion = {
    ttfnid: 'ttfnid',
    name: 'name',
    swissTimetableFieldNumber: 'asdf',
    status: 'ACTIVE',
    validFrom: '2021-06-01',
    validTo: '2029-06-01',
  };

  constructor(private timetableFieldNumbersService: TimetableFieldNumbersService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    const id = route.paramMap.get('id') || '';
    return id === 'add'
      ? of(this.newVersion)
      : this.timetableFieldNumbersService.getVersion(parseInt(id));
  }
}
