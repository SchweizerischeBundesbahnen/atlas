import { Injectable, OnDestroy } from '@angular/core';
import { TableService } from '../../core/components/table/table.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Pages } from '../pages';
import { Subscription } from 'rxjs';

@Injectable()
export class TthTableService extends TableService implements OnDestroy {
  private readonly tabRouteRegex: RegExp = new RegExp(
    `(${Pages.TTH_ACTIVE.path}|${Pages.TTH_PLANNED.path}|${Pages.TTH_ARCHIVED.path})`
  );
  private oldTabPath: string | null = null;
  private navigationEndSubscription: Subscription;

  constructor(private readonly router: Router) {
    super();
    this.navigationEndSubscription = this.router.events
      .pipe(
        filter((routerEvent): routerEvent is NavigationEnd => routerEvent instanceof NavigationEnd)
      )
      .subscribe((navigationEnd) => {
        // compare newTabPath to oldTabPath => reset table settings or nothing
        const matches: RegExpMatchArray | null = navigationEnd.url.match(this.tabRouteRegex);
        const newTabPath: string | null = this.getTabPathFromRegexMatch(matches);
        // reset or nothing
        if (this.oldTabPath !== newTabPath) {
          this.reset();
          this.oldTabPath = newTabPath;
        }
      });
  }

  ngOnDestroy(): void {
    this.navigationEndSubscription.unsubscribe();
  }

  private reset(): void {
    this.pageSize = 10;
    this.pageIndex = 0;
    this.sortActive = '';
    this.sortDirection = 'asc';
  }

  private getTabPathFromRegexMatch(matches: RegExpMatchArray | null): string | null {
    if (!matches) {
      return null;
    }
    return matches['0'];
  }
}
