import {Injectable} from '@angular/core';
import {PermissionService} from "./permission.service";
import {Pages} from "../../pages/pages";
import {Page} from "../model/page";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class PageService {

  private viewablePages: Page[] = [...Pages.pages];

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
}
