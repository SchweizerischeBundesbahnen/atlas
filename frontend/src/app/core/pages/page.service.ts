import {Injectable} from '@angular/core';
import {Pages} from "../../pages/pages";
import {Page} from "../model/page";
import {PermissionService} from "../auth/permission/permission.service";
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
    if (this.permissionService.mayAccessBulkImport() && environment.bulkImportEnabled) {
      this.viewablePages.push(Pages.BULK_IMPORT);
    }
    if (this.permissionService.isAdmin) {
      this.viewablePages.push(...Pages.adminPages);
    }
  }

  resetPages() {
    this.viewablePages = [...Pages.pages];
  }

  get enabledPages() {
    return this.viewablePages;
  }

}
