import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EMPTY } from 'rxjs';
import { ClientCredentialAdministrationService } from './client-credential-administration.service';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ApplicationType } from '../../model/applicationType';
import { Permission } from '../../model/permission';
import { ClientCredentialCreate } from '../../model/clientCredentialCreate';
import { UserService } from '../../../core/auth/user/user.service';

const CLIENT_CREDENTIAL_BASE_PATH = '/user-administration/v1/client-credentials';

describe('ClientCredentialAdministrationService', () => {
  let service: ClientCredentialAdministrationService;
  let apiService: AtlasApiService;
  let httpParams: HttpParams;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClientCredentialAdministrationService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });

    service = TestBed.inject(ClientCredentialAdministrationService);
    apiService = TestBed.inject(AtlasApiService);
    vi.spyOn(apiService, 'paramsOf');
    vi.spyOn(apiService, 'get').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'post').mockImplementation(() => EMPTY);
    vi.spyOn(apiService, 'put').mockImplementation(() => EMPTY);
  });

  it('should getClientCredentials with pagination', () => {
    const sort = ['name,asc'];
    service.getClientCredentials(1, 10, sort);

    expect(apiService.paramsOf).toHaveBeenCalledExactlyOnceWith({
      page: 1,
      size: 10,
      sort,
    });
    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(CLIENT_CREDENTIAL_BASE_PATH, expect.any(HttpParams));
  });

  it('should getClientCredential by id', () => {
    service.getClientCredential('client-id');

    expect(apiService.get).toHaveBeenCalledExactlyOnceWith(`${CLIENT_CREDENTIAL_BASE_PATH}/client-id`);
  });

  it('should createClientCredential', () => {
    const createPayload: ClientCredentialCreate = {} as ClientCredentialCreate;

    service.createClientCredential(createPayload);

    expect(apiService.post).toHaveBeenCalledExactlyOnceWith(CLIENT_CREDENTIAL_BASE_PATH, createPayload);
  });

  it('should updateClientCredentialPermissions', () => {
    const permission: Permission = {} as Permission;

    service.updateClientCredentialPermissions('client-id', ApplicationType.Sepodi, permission);

    expect(apiService.put).toHaveBeenCalledExactlyOnceWith(
      `${CLIENT_CREDENTIAL_BASE_PATH}/client-id/${ApplicationType.Sepodi}`,
      permission
    );
  });
});
