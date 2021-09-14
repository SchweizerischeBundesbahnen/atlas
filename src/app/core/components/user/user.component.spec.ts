import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserComponent } from './user.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../auth/auth.service';
import { By } from '@angular/platform-browser';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { User } from './user';
import { EventEmitter } from '@angular/core';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;
  const user: User = {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    roles: ['role1', 'role2', 'role3'],
  };
  const eventEmitterUser: EventEmitter<User> = new EventEmitter<User>();

  const authServiceMock: Partial<AuthService> = {
    claims: { name: 'Test (ITC)', email: 'test@test.ch', roles: ['role1', 'role2', 'role3'] },
    logout: () => Promise.resolve(true),
    login: () => Promise.resolve(true),
    eventEmitter: eventEmitterUser,
  };

  const userName = authServiceMock.claims!.name;
  const expectedUserName = userName.substr(0, userName.indexOf('(')).trim();

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
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    eventEmitterUser.emit(user);
    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Component Rendering', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render username on the title', () => {
      expect(fixture.nativeElement.querySelector('button').title).toContain(expectedUserName);
    });

    it('should show user menu', () => {
      authServiceMock.eventEmitter?.emit(user);
      fixture.detectChanges();
      fixture.componentInstance.user = user;
      fixture.componentInstance.isAuthenticated = true;
      fixture.componentInstance.authenticate();
      fixture.detectChanges();
      const userMenuOpenButton = fixture.debugElement.query(By.css('#user-menu-button'));
      userMenuOpenButton.nativeElement.click();
      fixture.detectChanges();

      const usernameModal = fixture.debugElement.query(By.css('.user-info-modal')).nativeElement;
      expect(usernameModal.querySelector('.user-name-modal').textContent).toContain(
        expectedUserName
      );
      fixture.detectChanges();

      const userRolesModal = fixture.debugElement.query(By.css('#user-roles-modal')).nativeElement;
      expect(userRolesModal.querySelector('.user-info-modal').textContent).toContain(
        'PROFILE.YOUR_ROLES'
      );
      fixture.detectChanges();

      const userRoles = userRolesModal.querySelectorAll('mat-list>mat-list-item.mat-list-item');
      expect(userRoles[0].textContent).toContain(user.roles[0]);
      expect(userRoles[1].textContent).toContain(user.roles[1]);
      expect(userRoles[2].textContent).toContain(user.roles[2]);
    });

    it('should logout', () => {
      const logoutSpy = spyOn(authServiceMock as AuthService, 'logout');

      // Open user menu
      const usermenuOpenButton = fixture.debugElement.query(By.css('button'));
      usermenuOpenButton.nativeElement.click();
      fixture.detectChanges();

      // Logout
      const logoutButton = fixture.debugElement.query(By.css('#logout'));
      logoutButton.nativeElement.click();

      expect(logoutSpy).toHaveBeenCalled();
    });

    it('should login', () => {
      const loginSpy = spyOn(authServiceMock as AuthService, 'login');
      component.isAuthenticated = false;
      fixture.detectChanges();

      // Logout
      const logoutButton = fixture.debugElement.query(By.css('#login'));
      logoutButton.nativeElement.click();

      expect(loginSpy).toHaveBeenCalled();
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

    it('should be authenticate when user is not null', () => {
      //when
      component.authenticate();

      //then
      expect(component.isAuthenticated).toBeTruthy();
    });

    it('should not be authenticate when user is null', () => {
      //given
      component.user = undefined;

      //when
      component.authenticate();

      //then
      expect(component.isAuthenticated).toBeFalse();
    });

    it('should show roles', () => {
      //given
      component.user = {
        name: 'Test (ITC)',
        email: 'test@test.ch',
        roles: ['role1', 'role2', 'role3'],
      };
      //when
      const result = component.showRoles();

      //then
      expect(result).toBeTruthy();
    });

    it('should not show roles', () => {
      //given
      if (component.user) {
        component.user.roles = [];
      }
      //when
      const result = component.showRoles();

      //then
      expect(result).toBeFalsy();
    });
  });
});
