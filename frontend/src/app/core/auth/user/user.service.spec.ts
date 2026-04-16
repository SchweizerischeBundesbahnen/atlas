import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('UserService', () => {
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), UserService],
    });
    userService = TestBed.inject(UserService);
    const httpTesting = TestBed.inject(HttpTestingController);
    httpTesting.match({ method: 'GET' }).forEach((request) => {
      request.flush({
        displayName: 'Test (ITC)',
        mail: 'test@test.ch',
        sbbUserId: 'e123456',
        permissions: [],
      });
    });
  });

  it('should set current user and load permissions', () => {
    userService.setCurrentUserAndLoadPermissions({
      name: 'Test (ITC)',
      email: 'test@test.ch',
      sbbuid: 'e123456',
      isAdmin: true,
      permissions: [],
    });

    expect(userService.loggedIn).toBe(true);
    expect(userService.isAdmin).toBe(true);
    expect(userService.permissions).toEqual([]);
  });

  it('should set current user and reset', () => {
    userService.setCurrentUserAndLoadPermissions({
      name: 'Test (ITC)',
      email: 'test@test.ch',
      sbbuid: 'e123456',
      isAdmin: true,
      permissions: [],
    });

    expect(userService.loggedIn).toBe(true);
    userService.setToUnauthenticatedUser();

    expect(userService.loggedIn).toBe(false);
    expect(userService.isAdmin).toBe(false);
    expect(userService.permissions).toEqual([]);
  });
});
