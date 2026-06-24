import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { UserOpenInMailComponent } from './user-open-in-mail.component';
import { inputBinding, signal } from '@angular/core';
import { ApplicationType, Permission, User } from '../../../api';
import { of } from 'rxjs';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';

describe('UserOpenInMailComponent', () => {
  let component: UserOpenInMailComponent;
  let fixture: ComponentFixture<UserOpenInMailComponent>;

  const user: User = {
    permissions: new Set<Permission>(),
    sbbUserId: '',
    userId: 'e123456',
    displayName: 'Marek Hamsik',
    mail: 'asd@as.ch',
  };

  const userAdminServiceMock = {
    getUser() {
      return of(user);
    },
  };

  beforeEach(() => {
    // Config
    TestBed.configureTestingModule({
      providers: [{ provide: UserAdministrationService, useValue: userAdminServiceMock }],
    });

    // Arrangement
    const applicationTypeInputName: keyof UserOpenInMailComponent = 'applicationType';
    const userIdInputName: keyof UserOpenInMailComponent = 'userId';
    fixture = TestBed.createComponent(UserOpenInMailComponent, {
      bindings: [
        inputBinding(applicationTypeInputName, signal(ApplicationType.Prm)),
        inputBinding(userIdInputName, signal('e123456')),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open mail', () => {
    vi.spyOn(window, 'open').mockImplementation(() => null);
    component.openInMail();
    expect(window.open).toHaveBeenCalledExactlyOnceWith('mailto:asd@as.ch', '_self');
  });
});
