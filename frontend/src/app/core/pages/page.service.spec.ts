import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { PageService } from './page.service';
import { PermissionService, TthApplicationUserType } from '../auth/permission/permission.service';
import { Pages } from '../../pages/pages';
import { firstValueFrom } from 'rxjs';

const permissionServiceMock: Partial<PermissionService> = {
  mayAccessBulkImport: () => true,
  mayAccessTimetableHearing: () => true,
  mayAccessTtfn: () => true,
  isAdmin: true,
  getTthApplicationUserType: () => 'CANTON_TTH' as TthApplicationUserType,
  isTthBoUser: () => false,
};

describe('PageService', () => {
  let pageService: PageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PageService, { provide: PermissionService, useValue: permissionServiceMock }],
    });
    pageService = TestBed.inject(PageService);
  });

  it('should provide default pages', async () => {
    const enabledPages = await firstValueFrom(pageService.enabledPages);
    expect(enabledPages).toHaveLength(6);
  });

  it('should add all pages if allowed', async () => {
    pageService.addPagesBasedOnPermissions();
    const enabledPages = await firstValueFrom(pageService.enabledPages);
    expect(enabledPages).toHaveLength(9);
  });

  it('should remove TTH subpages for BO_TTH user', async () => {
    permissionServiceMock.getTthApplicationUserType = () => 'BO_TTH' as TthApplicationUserType;

    pageService.addPagesBasedOnPermissions();

    const enabledPages = await firstValueFrom(pageService.enabledPages);
    const tthPage = enabledPages.find((p) => p.path === Pages.TTH.path);
    expect(tthPage).toBeDefined();
    expect(tthPage?.subpages).toBeUndefined();
  });

  it('should keep TTH subpages for CANTON_TTH user', async () => {
    permissionServiceMock.getTthApplicationUserType = () => 'CANTON_TTH' as TthApplicationUserType;
    pageService.addPagesBasedOnPermissions();

    const enabledPages = await firstValueFrom(pageService.enabledPages);
    const tthPage = enabledPages.find((p) => p.path === Pages.TTH.path);
    expect(tthPage).toBeDefined();
    expect(tthPage?.subpages).toBeDefined();
  });
});
