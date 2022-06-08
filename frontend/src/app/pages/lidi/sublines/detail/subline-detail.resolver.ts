import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, of } from 'rxjs';
import { SublinesService, SublineVersion } from '../../../../api';

@Injectable({ providedIn: 'root' })
export class SublineDetailResolver implements Resolve<Array<SublineVersion>> {
  constructor(private sublinesService: SublinesService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<SublineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add' ? of([]) : this.sublinesService.getSublineVersion(idParameter);
  }
}
