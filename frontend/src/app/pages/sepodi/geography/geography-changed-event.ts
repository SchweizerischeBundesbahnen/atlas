import { BehaviorSubject, Observable } from 'rxjs';

export class GeographyChangedEvent {
  private _geographyChanged = new BehaviorSubject<boolean>(false);

  emit(value: boolean) {
    this._geographyChanged.next(value);
  }

  emitOnlyWhenValueChanged(value: boolean) {
    if (this._geographyChanged.value !== value) {
      this._geographyChanged.next(value);
    }
  }

  get(): Observable<boolean> {
    return this._geographyChanged.asObservable();
  }
}
