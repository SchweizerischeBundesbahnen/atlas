import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { SideNavComponent } from './side-nav.component';
import { By } from '@angular/platform-browser';
import { Router, RouterModule } from '@angular/router';
import { Pages } from '../../../pages/pages';
import { pageServiceMock, translateServiceProvider } from '../../../app.testing.mocks';
import { PageService } from '../../pages/page.service';
import { Page } from '../../model/page';
import { OverviewToTabShareDataService } from '../../../pages/tth/overview-tab/service/overview-to-tab-share-data.service';
import { LidiOverviewComponent } from '../../../pages/lidi/overview/lidi-overview.component';
import { TimetableFieldNumberOverviewComponent } from '../../../pages/ttfn/overview/timetable-field-number-overview.component';

describe('SideNavComponent', () => {
  let component: SideNavComponent;
  let fixture: ComponentFixture<SideNavComponent>;

  let router: Router;
  let overviewTabService: OverviewToTabShareDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([
          {
            path: Pages.LIDI.path,
            component: LidiOverviewComponent,
          },
          {
            path: Pages.TTFN.path,
            component: TimetableFieldNumberOverviewComponent,
          },
        ]),
      ],
      providers: [
        translateServiceProvider,
        {
          provide: PageService,
          useValue: pageServiceMock,
        },
        OverviewToTabShareDataService,
      ],
    });

    overviewTabService = TestBed.inject(OverviewToTabShareDataService);
    fixture = TestBed.createComponent(SideNavComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should show side-nav', () => {
    fixture.detectChanges();

    const result = fixture.debugElement.queryAll(By.css('a'));

    expect(result).toBeDefined();
    expect(result[0].nativeElement.textContent.trim()).toBe(Pages.pages[0].titleMenu);
    expect(result[1].nativeElement.textContent.trim()).toBe(Pages.pages[1].titleMenu);
  });

  it('home route should be active', () => {
    assertActiveNavItem('PAGES.HOME_MENU');
  });

  it('line directory route should be active', async () => {
    await router.navigate(['line-directory']);
    assertActiveNavItem('PAGES.LIDI.TITLE_MENU');
  });

  const assertActiveNavItem = (pageTitle: string) => {
    fixture.detectChanges();

    const navItems = fixture.debugElement.queryAll(By.css('a'));
    const activeNavItemIndex = navItems.findIndex((item) => Object.keys(item.classes).includes('route-active'));

    expect(navItems[activeNavItemIndex].nativeNode.querySelector('span').textContent).toBe(pageTitle);
  };

  it('should set activePageIndex correct', () => {
    const currentUrl = '/service-point-directory';
    component['setActivePage'](currentUrl, Pages.pages);

    expect(component.activePageIndex).toBe(3);
  });

  it('should set activeSubPageIndex correct', () => {
    const currentUrl = '/service-point-directory/workflows';
    component['setActivePage'](currentUrl, Pages.pages);

    expect(component.activeSubPageIndex).toBe(0);
    expect(component.activePageIndex).toBeNull();
  });

  it('should set index for unknown page to 0', () => {
    component['setActivePage']('/unknown', Pages.pages);

    expect(component.activePageIndex).toBe(0);
    expect(component.activeSubPageIndex).toBe(0);
  });

  it('should return path array without params when page has no params', () => {
    const subPage: Page = { title: '', path: 'dossier', params: [] };
    const page: Page = {
      title: '',
      path: '/tth',
      params: [],
      subpages: [subPage],
    };

    component.selectedPage.set(page);

    const result = component.subPageLinks();

    expect(result.get(subPage)).toEqual(['/tth', 'dossier']);
  });

  it('should return path array without params when page.params is undefined', () => {
    const subPage: Page = { title: '', path: 'dossier' };
    const page: Page = {
      title: '',
      path: '/tth',
      subpages: [subPage],
    };

    component.selectedPage.set(page);

    const result = component.subPageLinks();

    expect(result.get(subPage)).toEqual(['/tth', 'dossier']);
  });

  it('should include param values in path array when params exist', () => {
    overviewTabService.setCantonShort('be');

    const subPage: Page = { title: '', path: 'dossier' };
    const page: Page = {
      title: '',
      path: '/tth',
      params: ['canton'],
      subpages: [subPage],
    };

    component.selectedPage.set(page);

    const result = component.subPageLinks();

    expect(result.get(subPage)).toEqual(['/tth', 'be', 'dossier']);
  });
});
