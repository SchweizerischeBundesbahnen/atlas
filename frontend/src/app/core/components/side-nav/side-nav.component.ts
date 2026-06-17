import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Page } from '../../model/page';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, map, switchMap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PageService } from '../../pages/page.service';
import { AsyncPipe, NgClass } from '@angular/common';
import { MatListItem } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';
import { OverviewToTabShareDataService } from '../../../pages/tth/overview-tab/service/overview-to-tab-share-data.service';
import { Cantons } from '../../cantons/Cantons';

@Component({
  selector: 'atlas-side-nav',
  templateUrl: './side-nav.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./side-nav.component.scss'],
  imports: [MatListItem, RouterLink, NgClass, AsyncPipe, TranslatePipe],
  providers: [TranslatePipe],
})
export class SideNavComponent {
  activePageIndex: number | null = 0;
  activeSubPageIndex = 0;
  selectedPage = signal<Page | null>(null);

  readonly navParam = computed<Record<string, string>>(() => ({
    canton: this.overviewToTabService.cantonShort() || Cantons.swiss.path,
  }));

  subPageLinks = computed(() => {
    const nav = this.navParam();
    const page = this.selectedPage();

    if (!page?.subpages) {
      return new Map<Page, string[]>();
    }

    const links = new Map<Page, string[]>();
    for (const subPage of page.subpages) {
      const params = page.params ?? [];

      if (params.length > 0) {
        const values = params.map((param: string) => nav[param].toLowerCase()).filter(Boolean);

        links.set(subPage, [page.path, ...values, subPage.path]);
      } else {
        links.set(subPage, [page.path, subPage.path]);
      }
    }

    return links;
  });

  private readonly router = inject(Router);
  protected readonly pageService = inject(PageService);
  private readonly overviewToTabService = inject(OverviewToTabShareDataService);

  constructor() {
    this.router.events
      .pipe(
        takeUntilDestroyed(),
        filter((event) => event instanceof NavigationEnd),
        switchMap((event) => {
          return this.pageService.enabledPages.pipe(map((pages) => [event, pages] as [NavigationEnd, Page[]]));
        })
      )
      .subscribe(([event, pages]) => {
        this.setActivePage(event.url, pages);
      });
  }

  private setActivePage(currentUrl: string, pages: Page[]): void {
    pages.forEach((page, index) => {
      if (currentUrl.includes(page.path)) {
        this.activePageIndex = index;
        this.selectedPage.set(page);
        if (page.subpages) {
          page.subpages.forEach((subPage, index) => {
            if (currentUrl.includes(subPage.path)) {
              this.activeSubPageIndex = index;
              this.activePageIndex = null;
            }
          });
        }
      }
    });
  }
}
