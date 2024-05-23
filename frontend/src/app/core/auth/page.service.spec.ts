import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterModule} from "@angular/router";
import {PageService} from "./page.service";
import {PermissionService} from "./permission.service";

let mayAccessTth = true;
let mayAccessTtfn = true;
let admin = true;
const permissionServiceMock: Partial<PermissionService> = {
  mayAccessTimetableHearing: () => mayAccessTth,
  mayAccessTtfn: () => mayAccessTtfn,
  isAdmin: admin
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

});
