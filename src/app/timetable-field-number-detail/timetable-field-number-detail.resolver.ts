import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { TimetableFieldNumbersService } from '../api';

@Injectable({ providedIn: 'root' })
export class TimetableFieldNumberDetailResolver implements Resolve<any> {
  constructor(private timetableFieldNumbersService: TimetableFieldNumbersService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    const id = parseInt(route.paramMap.get('id')!);
    return this.timetableFieldNumbersService.getVersion(id);
  }
}
