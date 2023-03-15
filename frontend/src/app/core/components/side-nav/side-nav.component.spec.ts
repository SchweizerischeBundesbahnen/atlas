import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SideNavComponent } from './side-nav.component';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { Pages } from '../../../pages/pages';
import { LidiOverviewComponent } from '../../../pages/lidi/overview/lidi-overview.component';
import { TimetableFieldNumberOverviewComponent } from '../../../pages/ttfn/overview/timetable-field-number-overview.component';
import { AuthService } from '../../auth/auth.service';

describe('SideNavComponent', () => {
  let component: SideNavComponent;
  let fixture: ComponentFixture<SideNavComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SideNavComponent],
      imports: [
        RouterTestingModule.withRoutes([
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
      ],
      providers: [
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj<AuthService>(
            {},
            {
              roles: [],
            }
          ),
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
    expect(result[0].nativeElement.textContent.trim()).toBe(component.enabledPages[0].titleMenu);
    expect(result[1].nativeElement.textContent.trim()).toBe(component.enabledPages[1].titleMenu);
  });

  it('home route should be active', () => {
    assertActiveNavItem('PAGES.HOME_MENU');
  });

  it('timetable route should be active', async () => {
    await router.navigate(['timetable-field-number']);
    fixture.detectChanges();
    assertActiveNavItem('PAGES.TTFN.TITLE_MENU');
  });

  it('line directory route should be active', async () => {
    await router.navigate(['line-directory']);
    fixture.detectChanges();
    assertActiveNavItem('PAGES.LIDI.TITLE_MENU');
  });

  const assertActiveNavItem = (pageTitle: string) => {
    const navItems = fixture.debugElement.queryAll(By.css('div'));
    const activeNavItemIndex = navItems.findIndex((item) =>
      Object.keys(item.classes).includes('route-active')
    );

    expect(navItems[activeNavItemIndex].nativeNode.querySelector('span').textContent).toBe(
      pageTitle
    );
  };
});
