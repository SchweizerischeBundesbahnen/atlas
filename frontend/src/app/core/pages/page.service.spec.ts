import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterModule} from "@angular/router";
import {PageService} from "./page.service";
import {Pages} from "../../pages/pages";
import {PermissionService} from "../auth/permission/permission.service";

const permissionServiceMock: Partial<PermissionService> = {
  mayAccessMassImport: () => true,
  mayAccessTimetableHearing: () => true,
  mayAccessTtfn: () => true,
  isAdmin: true
};

describe('PageService', () => {

  let pageService: PageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterModule.forRoot([]),
      ],
      providers: [
        PageService,
        {provide: PermissionService, useValue: permissionServiceMock},
      ],
    });
    pageService = TestBed.inject(PageService);
  });

  it('should provide default pages', () => {
    const enabledPages = pageService.enabledPages;
    expect(enabledPages).toHaveSize(5);
  });

  it('should add all pages if allowed', () => {
    pageService.addPagesBasedOnPermissions();

    const enabledPages = pageService.enabledPages;
    expect(enabledPages).toHaveSize(9);
  });

  it('should reset pages', () => {
    pageService.addPagesBasedOnPermissions();
    expect(pageService.enabledPages).toHaveSize(9);

    pageService.resetPages();
    expect(pageService.enabledPages).toHaveSize(5);
  });

  it('should return submenu when', () => {

    const result = pageService.enabledPages.filter(i => i === Pages.SEPODI)[0];

    expect(result.subpages!.length).toBe(1);
    expect(result.subpages![0].title).toBe('PAGES.WORKFLOW.TITLE_HEADER');
  });

});
