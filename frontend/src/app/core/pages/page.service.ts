import { Injectable } from '@angular/core';
import { Pages } from '../../pages/pages';
import { Page } from '../model/page';
import { PermissionService } from '../auth/permission/permission.service';
import { environment } from '../../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PageService {
  private _viewablePages: BehaviorSubject<Page[]> = new BehaviorSubject([...Pages.pages]);
  enabledPages: Observable<Page[]> = this._viewablePages.asObservable();

  constructor(private readonly permissionService: PermissionService) {}

  addPagesBasedOnPermissions() {
    const pagesToAdd: Page[] = [
      ...(this.permissionService.mayAccessTimetableHearing() ? [Pages.TTH] : []),
      ...(this.permissionService.mayAccessTtfn() ? [Pages.TTFN] : []),
      ...(this.permissionService.mayAccessBulkImport() && environment.bulkImportEnabled
        ? [Pages.BULK_IMPORT]
        : []),
      ...(this.permissionService.isAdmin ? [...Pages.adminPages] : []),
    ];

    this._viewablePages.next([...this._viewablePages.value, ...pagesToAdd]);
  }

  resetPages() {
    this._viewablePages.next([...Pages.pages]);
  }
}
