import {Component} from '@angular/core';
import {Page} from '../../model/page';
import {NavigationEnd, Router} from '@angular/router';
import {filter, tap} from 'rxjs/operators';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {PageService} from "../../pages/page.service";

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
})
export class SideNavComponent {
  activePageIndex: number | null = 0;
  activeSubPageIndex = 0;

  selectedPage: Page | null = null;

  constructor(private readonly router: Router, private pageService:PageService) {
    this.router.events
      .pipe(
        takeUntilDestroyed(),
        filter((event) => event instanceof NavigationEnd),
        tap(() => (this.getActivePageIndex(this.router.url))),
      )
      .subscribe();
  }

  get enabledPages(): Page[] {
    return this.pageService.enabledPages;
  }

  getActivePageIndex(currentUrl: string): void {
    this.enabledPages.forEach((page, index) => {
      if (currentUrl.includes(page.path)) {
        this.activePageIndex = index;
        this.selectedPage = page
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
