import { Component, OnDestroy } from '@angular/core';
import { Page } from '../../model/page';
import { Pages } from '../../../pages/pages';
import { NavigationEnd, Router } from '@angular/router';
import { filter, takeUntil, tap } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
})
export class SideNavComponent implements OnDestroy {
  readonly pages: Page[] = Pages.pages;
  private readonly ngUnsubscribe = new Subject<void>();
  private currentUrl = '/';

  constructor(private readonly router: Router) {
    this.router.events
      .pipe(
        takeUntil(this.ngUnsubscribe),
        filter((event) => event instanceof NavigationEnd),
        tap(() => (this.currentUrl = this.router.url))
      )
      .subscribe();
  }

  isRouteActive(path: string): boolean {
    if (path.length === 0 && this.currentUrl === '/') return true;
    else if (path.length > 0 && this.currentUrl.includes(path)) return true;
    return false;
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.complete();
  }
}
