import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Cantons } from '../overview/canton/Cantons';

@Injectable({
  providedIn: 'root',
})
export class OverviewToTabShareDataService {
  private cantonShort = new BehaviorSubject(Cantons.swiss.path);
  cantonShort$ = this.cantonShort.asObservable();

  changeData(cantonShort: string) {
    this.cantonShort.next(cantonShort);
  }
}
