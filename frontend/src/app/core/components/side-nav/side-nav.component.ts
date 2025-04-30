import { Component } from '@angular/core';
import { Page } from '../../model/page';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, map, switchMap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PageService } from '../../pages/page.service';
import { NgFor, NgClass, NgIf, AsyncPipe } from '@angular/common';
import { MatListItem } from '@angular/material/list';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
  imports: [
    NgFor,
    MatListItem,
    RouterLink,
    NgClass,
    NgIf,
    AsyncPipe,
    TranslatePipe,
  ],
})
export class SideNavComponent {
  activePageIndex: number | null = 0;
  activeSubPageIndex = 0;
  selectedPage: Page | null = null;

  constructor(
    private readonly router: Router,
    protected readonly pageService: PageService
  ) {
    this.router.events
      .pipe(
        takeUntilDestroyed(),
        filter((event) => event instanceof NavigationEnd),
        switchMap((event) => {
          return pageService.enabledPages.pipe(
            map((pages) => [event, pages] as [NavigationEnd, Page[]])
          );
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
        this.selectedPage = page;
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
