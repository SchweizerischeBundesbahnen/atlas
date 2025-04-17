import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SideNavComponent } from './side-nav.component';
import { By } from '@angular/platform-browser';
import { Router, RouterModule } from '@angular/router';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
} from '@ngx-translate/core';
import { Pages } from '../../../pages/pages';
import { LidiOverviewComponent } from '../../../pages/lidi/overview/lidi-overview.component';
import { TimetableFieldNumberOverviewComponent } from '../../../pages/ttfn/overview/timetable-field-number-overview.component';
import { pageServiceMock } from '../../../app.testing.mocks';
import { PageService } from '../../pages/page.service';

describe('SideNavComponent', () => {
  let component: SideNavComponent;
  let fixture: ComponentFixture<SideNavComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
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
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        SideNavComponent,
      ],
      providers: [
        {
          provide: PageService,
          useValue: pageServiceMock,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideNavComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show side-nav', () => {
    const result = fixture.debugElement.queryAll(By.css('a'));
    expect(result).toBeDefined();
    expect(result[0].nativeElement.textContent.trim()).toBe(
      Pages.pages[0].titleMenu
    );
    expect(result[1].nativeElement.textContent.trim()).toBe(
      Pages.pages[1].titleMenu
    );
  });

  it('home route should be active', () => {
    assertActiveNavItem('PAGES.HOME_MENU');
  });

  it('line directory route should be active', async () => {
    await router.navigate(['line-directory']);
    fixture.detectChanges();
    assertActiveNavItem('PAGES.LIDI.TITLE_MENU');
  });

  const assertActiveNavItem = (pageTitle: string) => {
    const navItems = fixture.debugElement.queryAll(By.css('a'));
    const activeNavItemIndex = navItems.findIndex((item) =>
      Object.keys(item.classes).includes('route-active')
    );

    expect(
      navItems[activeNavItemIndex].nativeNode.querySelector('span').textContent
    ).toBe(pageTitle);
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
});
