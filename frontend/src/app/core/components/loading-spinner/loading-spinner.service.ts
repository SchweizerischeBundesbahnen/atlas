import { Injectable } from '@angular/core';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
} from '@angular/router';
import { BehaviorSubject, merge, of } from 'rxjs';
import { delay, filter, map, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LoadingSpinnerService {
  loading = new BehaviorSubject(false);

  constructor(private router: Router) {}

  /*initLoadingSpinner(): void {
    const navigationStart$ = this.router.events.pipe(
      filter((event) => event instanceof NavigationStart),
    );

    const navigationEnd$ = this.router.events.pipe(
      filter(
        (event) =>
          event instanceof NavigationEnd ||
          event instanceof NavigationCancel ||
          event instanceof NavigationError,
      ),
    );

    // short delay before navigation end, because of the NG100 error:
    const isLoadingStatus$ = navigationStart$.pipe(
      switchMap(() =>
        merge(
          of(true),
          navigationEnd$.pipe(
            delay(1),
            map(() => false),
          ),
        ),
      ),
    );

    isLoadingStatus$.subscribe((loading) => (this.loading = loading));
  }*/
}
