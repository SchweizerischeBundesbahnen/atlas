import {UserAdministrationService,} from '../../../api';
import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterModule} from "@angular/router";
import {UserService} from "./user.service";
import {of} from "rxjs";
import {ApiConfigService} from "../../configuration/api-config.service";

describe('UserService', () => {

  let userService: UserService;
  const userAdministrationService = jasmine.createSpyObj(['getCurrentUser']);
  userAdministrationService.getCurrentUser.and.returnValue(of({
    displayName: 'Test (ITC)',
    mail: 'test@test.ch',
    sbbUserId: 'e123456',
    permissions: []
  }));
  const apiConfigService = jasmine.createSpyObj<ApiConfigService>(['setToAuthenticatedUrl', 'setToUnauthenticatedUrl']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterModule.forRoot([]),
      ],
      providers: [
        UserService,
        {provide: UserAdministrationService, useValue: userAdministrationService},
        {provide: ApiConfigService, useValue: apiConfigService},
      ],
    });
    userService = TestBed.inject(UserService);
  });

  it('should set current user and load permissions', () => {
    userService.setCurrentUserAndLoadPermissions({
      name: 'Test (ITC)',
      email: 'test@test.ch',
      sbbuid: 'e123456',
      isAdmin: true,
      permissions: []
    });

    expect(userService.loggedIn).toBeTrue();
    expect(userAdministrationService.getCurrentUser).toHaveBeenCalled();
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
      permissions: []
    });

    expect(userService.loggedIn).toBeTrue();

    userService.resetCurrentUser();
    expect(apiConfigService.setToUnauthenticatedUrl).toHaveBeenCalled();
    expect(userService.loggedIn).toBeFalse();
    expect(userService.isAdmin).toBeFalse();
    expect(userService.permissions).toEqual([]);
  });

});
