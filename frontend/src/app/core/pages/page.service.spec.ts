import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterModule} from "@angular/router";
import {PageService} from "./page.service";
import {environment} from "../../../environments/environment";
import {Pages} from "../../pages/pages";
import {PermissionService} from "../auth/permission/permission.service";
import {Page} from "../model/page";

const permissionServiceMock: Partial<PermissionService> = {
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
    expect(enabledPages).toHaveSize(8);
  });

  it('should reset pages', () => {
    pageService.addPagesBasedOnPermissions();
    expect(pageService.enabledPages).toHaveSize(8);

    pageService.resetPages();
    expect(pageService.enabledPages).toHaveSize(5);
  });

  it('should not return submenu when sepodiWorkflowEnabled is false', () => {
    environment.sepodiWorkflowEnabled = false;

    const result = pageService.enabledPages.filter(i=> i.title===Pages.SEPODI.title)[0];

    expect(result.subpages!.length).toBe(0);
  });

  it('should return submenu when sepodiWorkflowEnabled is true', () => {
    environment.sepodiWorkflowEnabled = true;

    const result = pageService.enabledPages.filter(i=> i===Pages.SEPODI)[0];

    expect(result.subpages!.length).toBe(1);
    expect(result.subpages![0].title).toBe('PAGES.WORKFLOW.TITLE_HEADER');
  });
});
