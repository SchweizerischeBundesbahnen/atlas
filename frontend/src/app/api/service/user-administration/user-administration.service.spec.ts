import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { EMPTY } from 'rxjs';

import { UserAdministrationService } from './user-administration.service';
import { ApplicationType } from '../../model/applicationType';
import { PermissionRestrictionType } from '../../model/permissionRestrictionType';
import { UserPermissionCreate } from '../../model/userPermissionCreate';
import { Permission } from '../../model/permission';

const USER_BASE_PATH = '/user-administration/v1/users';
const SEARCH_PATH = '/user-administration/v1/search';
const SEARCH_IN_ATLAS_PATH = '/user-administration/v1/search-in-atlas';

describe('UserAdministrationService', () => {
  let service: UserAdministrationService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserAdministrationService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(UserAdministrationService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'validateParams');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'delete').mockImplementation(() => EMPTY);
  });

  it('should search users with parameter propagation', () => {
    service.searchUsers('query');

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      searchQuery: 'query',
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(SEARCH_PATH, expect.any(HttpParams));
  });

  it('should search users in atlas with application type', () => {
    service.searchUsersInAtlas('query', ApplicationType.Prm);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      searchQuery: 'query',
      applicationType: ApplicationType.Prm,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(SEARCH_IN_ATLAS_PATH, expect.any(HttpParams));
  });

  it('should get users with complex filters', () => {
    const restrictions = new Set(['restriction']);
    const applicationTypes = new Set([ApplicationType.Sepodi]);
    const permissionType = PermissionRestrictionType.Canton;

    service.getUsers(0, 25, restrictions, permissionType, applicationTypes);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      permissionRestrictions: restrictions,
      type: permissionType,
      applicationTypes,
      page: 0,
      size: 25,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(USER_BASE_PATH, expect.any(HttpParams));
  });

  it('should request current user', () => {
    service.getCurrentUser();

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(`${USER_BASE_PATH}/current`);
  });

  it('should request user by id', () => {
    service.getUser('user-id');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(`${USER_BASE_PATH}/user-id`);
  });

  it('should request user display name', () => {
    service.getUserDisplayName('user-id');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(`${USER_BASE_PATH}/user-id/displayname`);
  });

  it('should create user permission with payload', () => {
    const payload = {} as UserPermissionCreate;

    service.createUserPermission(payload);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(USER_BASE_PATH, payload);
  });

  it('should update user permission with validation and put', () => {
    const newPermission = {} as Permission;

    service.updateUserPermission('user-id', ApplicationType.Ttfn, newPermission);

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({
      userId: 'user-id',
      application: ApplicationType.Ttfn,
      permission: newPermission,
    });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      `${USER_BASE_PATH}/user-id/${ApplicationType.Ttfn}`,
      newPermission
    );
  });

  it('should call the manual mail endpoints', () => {
    service.updateManualMail('user-id', 'manual@sbb.ch');

    expect(apiService.validateParams).toHaveBeenCalledExactlyOnceWith({ userId: 'user-id' });
    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(`${USER_BASE_PATH}/user-id/manual-mail`, {
      mail: 'manual@sbb.ch',
    });

    service.deleteManualMail('user-id');

    expect(apiService.delete).toHaveBeenCalledExactlyOnceWith(`${USER_BASE_PATH}/user-id/manual-mail`);
  });
});
