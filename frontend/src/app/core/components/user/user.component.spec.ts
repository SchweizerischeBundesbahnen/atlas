import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserComponent} from './user.component';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {AuthService} from '../../auth/auth.service';
import {By} from '@angular/platform-browser';
import {MaterialModule} from '../../module/material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {adminUserServiceMock, authServiceSpy} from "../../../app.testing.mocks";
import {UserService} from "../../auth/user.service";

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserComponent],
      imports: [
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: UserService, useValue: adminUserServiceMock },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;

    component.init();
    fixture.detectChanges();
  });

  describe('Component Rendering', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render username on the title', () => {
      expect(fixture.nativeElement.querySelector('button').title).toContain('Test');
    });

    it('should show user menu', () => {
      adminUserServiceMock.userChanged?.next();
      fixture.detectChanges();

      const usernameModal = fixture.debugElement.query(By.css('.user-name')).nativeElement;
      expect(usernameModal.textContent).toContain('Test');
      fixture.detectChanges();

      const userMenuOpenButton = fixture.debugElement.query(By.css('#user-menu-button'));
      userMenuOpenButton.nativeElement.click();
      fixture.detectChanges();

      const userRolesModal = fixture.debugElement.query(By.css('#user-roles-modal')).nativeElement;
      expect(userRolesModal.querySelector('.user-info-modal').textContent).toContain(
        'PROFILE.YOUR_ROLES'
      );
      fixture.detectChanges();

      const userRoles = userRolesModal.querySelectorAll('.user-role-item');
      expect(userRoles[0].textContent).toContain('atlas-admin');
    });

    it('should logout', () => {
      // Open user menu
      const usermenuOpenButton = fixture.debugElement.query(By.css('button'));
      usermenuOpenButton.nativeElement.click();
      fixture.detectChanges();

      // Logout
      const logoutButton = fixture.debugElement.query(By.css('#logout'));
      logoutButton.nativeElement.click();

      expect(authServiceSpy.logout).toHaveBeenCalled();
    });

    it('should login', () => {
      component.isLoggedIn = false;
      fixture.detectChanges();

      // Login
      const loginButton = fixture.debugElement.query(By.css('#login'));
      loginButton.nativeElement.click();

      expect(authServiceSpy.login).toHaveBeenCalled();
    });
  });

  describe('Component logic', () => {
    it('should extract username', () => {
      //when
      component.extractUserName();

      //then
      expect(component.userName).toBe('Test');
    });

    it('should return null when name is null', () => {
      //given
      component.user = undefined;
      //when
      component.extractUserName();

      //then
      expect(component.userName).toBeUndefined();
    });

  });
});
