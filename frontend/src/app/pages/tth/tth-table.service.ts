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
  private oldTabPath: string | null;
  private navigationEndSubscription: Subscription;

  constructor(private readonly router: Router) {
    super();
    this.oldTabPath = this.getTabPathFromRegexMatches(this.router.url.match(this.tabRouteRegex));
    this.navigationEndSubscription = this.router.events
      .pipe(
        filter((routerEvent): routerEvent is NavigationEnd => routerEvent instanceof NavigationEnd)
      )
      .subscribe((navigationEnd) => {
        // compare newTabPath to oldTabPath: if different => reset table settings
        const matches: RegExpMatchArray | null = navigationEnd.urlAfterRedirects.match(
          this.tabRouteRegex
        );
        const newTabPath: string | null = this.getTabPathFromRegexMatches(matches);
        if (this.oldTabPath !== newTabPath) {
          this.resetTableSettings();
          this.oldTabPath = newTabPath;
        }
      });
  }

  ngOnDestroy(): void {
    this.navigationEndSubscription.unsubscribe();
  }

  private resetTableSettings(): void {
    this.pageSize = 10;
    this.pageIndex = 0;
    this.sortActive = '';
    this.sortDirection = 'asc';
  }

  private getTabPathFromRegexMatches(matches: RegExpMatchArray | null): string | null {
    return matches ? matches['0'] : null;
  }
}
