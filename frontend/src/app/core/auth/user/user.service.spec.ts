import { TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { UserService } from './user.service';
import { ApiConfigService } from '../../configuration/api-config.service';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';

describe('UserService', () => {
  let userService: UserService;
  const apiConfigService = jasmine.createSpyObj<ApiConfigService>([
    'setToAuthenticatedUrl',
    'setToUnauthenticatedUrl',
  ]);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        UserService,
        { provide: ApiConfigService, useValue: apiConfigService },
      ],
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

    expect(userService.loggedIn).toBeTrue();
    expect(apiConfigService.setToAuthenticatedUrl).toHaveBeenCalled();

    expect(userService.isAdmin).toBeTrue();
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

    expect(userService.loggedIn).toBeTrue();

    userService.setToUnauthenticatedUser();
    expect(apiConfigService.setToUnauthenticatedUrl).toHaveBeenCalled();
    expect(userService.loggedIn).toBeFalse();
    expect(userService.isAdmin).toBeFalse();
    expect(userService.permissions).toEqual([]);
  });
});
