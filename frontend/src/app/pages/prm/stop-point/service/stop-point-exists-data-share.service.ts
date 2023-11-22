import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StopPointExistsDataShareService {
  private isNew = new BehaviorSubject(false);
  isNew$ = this.isNew.asObservable();

  changeData(isNew: boolean) {
    this.isNew.next(isNew);
  }
}
