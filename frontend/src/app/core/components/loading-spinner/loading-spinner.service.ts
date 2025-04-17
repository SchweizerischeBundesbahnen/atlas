import { Injectable } from '@angular/core';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
} from '@angular/router';
import { BehaviorSubject, merge } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LoadingSpinnerService {
  loading = new BehaviorSubject(false);

  constructor(private readonly router: Router) {
    const navigationStart$ = this.router.events.pipe(
      filter((event) => event instanceof NavigationStart),
      map(() => true)
    );

    const navigationEnd$ = this.router.events.pipe(
      filter(
        (event) =>
          event instanceof NavigationEnd ||
          event instanceof NavigationCancel ||
          event instanceof NavigationError
      ),
      map(() => false)
    );

    merge(navigationStart$, navigationEnd$).subscribe((loading) => {
      this.loading.next(loading);
    });
  }
}
