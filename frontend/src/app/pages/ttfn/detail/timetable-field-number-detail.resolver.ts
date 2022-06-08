import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TimetableFieldNumbersService, TimetableFieldNumberVersion } from '../../../api';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver
  implements Resolve<Array<TimetableFieldNumberVersion>>
{
  constructor(private timetableFieldNumbersService: TimetableFieldNumbersService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<TimetableFieldNumberVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add'
      ? of([])
      : this.timetableFieldNumbersService.getAllVersionsVersioned(idParameter);
  }
}
