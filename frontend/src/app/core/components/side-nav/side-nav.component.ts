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
  activePageIndex = 0;
  selectedPage: Page | null = null;

  constructor(private readonly router: Router, private pageService:PageService) {
    this.router.events
      .pipe(
        takeUntilDestroyed(),
        filter((event) => event instanceof NavigationEnd),
        tap(() => (this.activePageIndex = this.getActivePageIndex(this.router.url))),
      )
      .subscribe();
  }

  get enabledPages(): Page[] {
    return this.pageService.enabledPages;
  }

  getActivePageIndex(currentUrl: string): number {
    this.selectPage(currentUrl)

    if (currentUrl === '/') {
      return this.enabledPages.findIndex((value) => value.path.length === 0);
    }
    return this.enabledPages.findIndex(
      (value) => value.path.length > 0 && currentUrl.includes(value.path),
    );
  }
  selectPage(currentUrl: string){
    this.enabledPages.findIndex(value => {
      if(currentUrl.includes(value.path)){
        this.selectedPage = value
      }
    })
  }
}
