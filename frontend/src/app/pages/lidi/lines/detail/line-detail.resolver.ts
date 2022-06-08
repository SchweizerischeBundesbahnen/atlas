import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, of } from 'rxjs';
import { LinesService, LineVersion } from '../../../../api';

@Injectable({ providedIn: 'root' })
export class LineDetailResolver implements Resolve<Array<LineVersion>> {
  constructor(private linesService: LinesService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Array<LineVersion>> {
    const idParameter = route.paramMap.get('id') || '';
    return idParameter === 'add' ? of([]) : this.linesService.getLineVersions(idParameter);
  }
}
