import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OverviewToTabService {
  private cantonShort = new BehaviorSubject('ch');
  cantonShort$ = this.cantonShort.asObservable();

  changeData(cantonShort: string) {
    this.cantonShort.next(cantonShort);
  }
}
