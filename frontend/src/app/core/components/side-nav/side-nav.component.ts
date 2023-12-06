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
  activePageIndex = 0;
  private readonly ngUnsubscribe = new Subject<void>();

  constructor(private readonly router: Router) {
    this.router.events
      .pipe(
        takeUntil(this.ngUnsubscribe),
        filter((event) => event instanceof NavigationEnd),
        tap(() => (this.activePageIndex = this.getActivePageIndex(this.router.url))),
      )
      .subscribe();
  }

  get enabledPages(): Page[] {
    return Pages.enabledPages;
  }

  getActivePageIndex(currentUrl: string): number {
    if (currentUrl === '/') {
      return this.enabledPages.findIndex((value) => value.path.length === 0);
    }
    return this.enabledPages.findIndex(
      (value) => value.path.length > 0 && currentUrl.includes(value.path),
    );
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
