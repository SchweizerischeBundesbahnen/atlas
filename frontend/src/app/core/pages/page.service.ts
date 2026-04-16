import { Injectable } from '@angular/core';
import { Pages } from '../../pages/pages';
import { Page } from '../model/page';
import { PermissionService } from '../auth/permission/permission.service';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PageService {
  private _viewablePages: BehaviorSubject<Page[]> = new BehaviorSubject([...Pages.pages, Pages.TTFN]);
  enabledPages: Observable<Page[]> = this._viewablePages.asObservable();

  constructor(private readonly permissionService: PermissionService) {}

  addPagesBasedOnPermissions() {
    const userType = this.permissionService.getTthApplicationUserType();
    const pagesToAdd: Page[] = [];

    if (this.permissionService.mayAccessTimetableHearing()) {
      const tthPage = userType === 'BO_TTH' ? { ...Pages.TTH, subpages: undefined } : Pages.TTH;
      pagesToAdd.push(tthPage);
    }

    if (this.permissionService.mayAccessBulkImport()) {
      pagesToAdd.push(Pages.BULK_IMPORT);
    }

    if (this.permissionService.isAdmin) {
      pagesToAdd.push(...Pages.adminPages);
    }

    this._viewablePages.next([...this._viewablePages.value, ...pagesToAdd]);
  }
}
