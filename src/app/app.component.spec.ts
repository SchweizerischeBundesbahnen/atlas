import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SbbUsermenuItem } from '@sbb-esta/angular-business/usermenu';
import { SbbIconTestingModule } from '@sbb-esta/angular-core/icon/testing';

import { AppComponent } from './app.component';
import { AuthService } from './core/auth.service';
import { SbbAngularLibraryModule } from './shared/sbb-angular-library.module';

const authServiceMock: Partial<AuthService> = {
  claims: { name: 'Test', email: 'test@test.ch', roles: [] },
  logout: () => Promise.resolve(true),
};

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        RouterTestingModule,
        SbbAngularLibraryModule,
        SbbIconTestingModule,
      ],
      declarations: [AppComponent],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render title', () => {
    expect(
      fixture.nativeElement.querySelector('.sbb-header-titlebox > span').textContent
    ).toContain('Timetable Field Number');
  });

  it('should render username', () => {
    expect(
      fixture.nativeElement.querySelector('.sbb-usermenu-user-info-display-name').textContent
    ).toContain(authServiceMock.claims!.name);
  });

  it('should logout', () => {
    const logoutSpy = spyOn(authServiceMock as AuthService, 'logout');

    // Open user menu
    const usermenuOpenButton = fixture.debugElement.query(By.css('.sbb-usermenu-trigger-open'));
    usermenuOpenButton.nativeElement.click();
    fixture.detectChanges();

    // Logout
    const logoutButton = fixture.debugElement.query(By.directive(SbbUsermenuItem));
    logoutButton.nativeElement.click();

    expect(logoutSpy).toHaveBeenCalled();
  });
});
