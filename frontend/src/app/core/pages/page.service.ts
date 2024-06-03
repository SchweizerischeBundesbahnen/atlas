import {Injectable} from '@angular/core';
import {Pages} from "../../pages/pages";
import {Page} from "../model/page";
import {environment} from "../../../environments/environment";
import {PermissionService} from "../auth/permission/permission.service";
import {Observable, Subject} from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class PageService {

  private viewablePages: Page[] = [...Pages.pages];
  private page = new Subject<Page>();
  constructor(private permissionService: PermissionService) {
  }

  addPagesBasedOnPermissions() {
    if (this.permissionService.mayAccessTimetableHearing()) {
      this.viewablePages.push(Pages.TTH);
    }
    if (this.permissionService.mayAccessTtfn()) {
      this.viewablePages.push(Pages.TTFN);
    }
    if (this.permissionService.isAdmin) {
      this.viewablePages.push(...Pages.adminPages);
    }
  }

  resetPages() {
    this.viewablePages = [...Pages.pages];
  }

  get enabledPages() {
    return this.viewablePages.map(page => {
      if (page === Pages.SEPODI && !environment.sepodiWorkflowEnabled) {
        return {
          ...page,
          subpages: page.subpages!.filter(subpage => subpage.title !== 'PAGES.WORKFLOW.TITLE_HEADER')
        };
      }
      return page;
    });
  }

  public getPage(): Observable<Page> {
    return this.page.asObservable();
  }

  public setPage(page: Page) {
    return this.page.next(page);
  }
}
