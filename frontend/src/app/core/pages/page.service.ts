import { Injectable } from '@angular/core';
import { Pages } from '../../pages/pages';
import { Page } from '../model/page';
import { PermissionService } from '../auth/permission/permission.service';
import { environment } from '../../../environments/environment';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PageService {
  private viewablePages: BehaviorSubject<Page[]> = new BehaviorSubject([...Pages.pages]);

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

    this.viewablePages.next([...this.viewablePages.value, ...pagesToAdd]);
  }

  resetPages() {
    this.viewablePages.next([...Pages.pages]);
  }

  get enabledPages() {
    return this.viewablePages.asObservable();
  }
}
