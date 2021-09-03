import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TimetableFieldNumbersService, Version } from '../api';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<Version> {
  newVersion: Version = {
    ttfnid: 'ttfnid',
    name: 'name',
    swissTimetableFieldNumber: 'asdf',
    status: 'ACTIVE',
    validFrom: new Date('2021-06-01'),
    validTo: new Date('2029-06-01'),
  };

  constructor(private timetableFieldNumbersService: TimetableFieldNumbersService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Version> {
    const id = route.paramMap.get('id') || '';
    return id === 'add'
      ? of(this.newVersion)
      : this.timetableFieldNumbersService.getVersion(parseInt(id));
  }
}
