import { TestBed } from '@angular/core/testing';
import { PageService } from './page.service';
import { PermissionService } from '../auth/permission/permission.service';

const permissionServiceMock: Partial<PermissionService> = {
  mayAccessBulkImport: () => true,
  mayAccessTimetableHearing: () => true,
  mayAccessTtfn: () => true,
  isAdmin: true,
};

describe('PageService', () => {
  let pageService: PageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PageService,
        { provide: PermissionService, useValue: permissionServiceMock },
      ],
    });
    pageService = TestBed.inject(PageService);
  });

  it('should provide default pages', (done) => {
    pageService.enabledPages.subscribe((enabledPages) => {
      expect(enabledPages).toHaveSize(5);
      done();
    });
  });

  it('should add all pages if allowed', (done) => {
    pageService.addPagesBasedOnPermissions();
    pageService.enabledPages.subscribe((enabledPages) => {
      expect(enabledPages).toHaveSize(9);
      done();
    });
  });

  it('should reset pages', (done) => {
    pageService.addPagesBasedOnPermissions();
    pageService.resetPages();
    pageService.enabledPages.subscribe((enabledPages) => {
      expect(enabledPages).toHaveSize(5);
      done();
    });
  });
});
