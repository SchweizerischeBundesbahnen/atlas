import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SideNavComponent } from './side-nav.component';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { Pages } from '../../../pages/pages';
import { LidiOverviewComponent } from '../../../pages/lidi/overview/lidi-overview.component';

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
        ]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
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
    const firstSideNavItem = result[0];
    const secondSideNavItem = result[1];
    expect(firstSideNavItem.nativeElement.textContent.trim()).toBe(component.pages[0].titleMenu);
    expect(secondSideNavItem.nativeElement.textContent.trim()).toBe(component.pages[1].titleMenu);
  });

  it('home route should be active', () => {
    expect(component.isRouteActive(component.pages[0].path)).toBeTrue();
  });

  it('timetable route should not be active', () => {
    expect(component.isRouteActive(component.pages[1].path)).toBeFalse();
  });

  it('line directory route should be active', async () => {
    await router.navigate(['line-directory']);
    expect(component.isRouteActive(component.pages[2].path)).toBeTrue();
    expect(component.isRouteActive(component.pages[1].path)).toBeFalse();
    expect(component.isRouteActive(component.pages[0].path)).toBeFalse();
  });
});
