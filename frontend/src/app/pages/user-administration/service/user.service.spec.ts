import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';
import {
  ClientCredentialAdministrationService,
  ContainerUser,
  User,
  UserAdministrationService,
  UserInformationService,
  UserPermissionCreate,
} from '../../../api';
import { of } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  const userAdministrationServiceMock = jasmine.createSpyObj('userAdministrationService', [
    'getUsers',
    'getUser',
  ]);
  const userInformationServiceMock = jasmine.createSpyObj('userInformationService', [
    'searchUsers',
  ]);

  let clientCredentialAdministrationServiceSpy;

  beforeEach(() => {
    userAdministrationServiceMock.getUsers.calls.reset();
    userAdministrationServiceMock.getUser.calls.reset();
    userInformationServiceMock.searchUsers.calls.reset();

    clientCredentialAdministrationServiceSpy = jasmine.createSpyObj([
      'getClientCredential',
      'createClientCredential',
      'updateClientCredential',
    ]);
    TestBed.configureTestingModule({
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceMock,
        },
        {
          provide: UserInformationService,
          useValue: userInformationServiceMock,
        },
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationServiceSpy,
        },
      ],
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('test getUsers', (done) => {
    userAdministrationServiceMock.getUsers.and.returnValue(
      of<ContainerUser>({
        totalCount: 5,
        objects: [{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }],
      }),
    );

    service.getUsers(10, 10).subscribe((res) => {
      expect(res).toEqual({
        users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }],
        totalCount: 5,
      });
      done();
    });
  });

  it('test getUser', (done) => {
    userAdministrationServiceMock.getUser.and.returnValue(of({ sbbUserId: 'u123456' }));

    service.getUser('u123456').subscribe((res) => {
      expect(userAdministrationServiceMock.getUser).toHaveBeenCalledOnceWith('u123456');
      expect(res).toEqual({ sbbUserId: 'u123456' });
      done();
    });
  });

  it('test searchUsers', (done) => {
    userInformationServiceMock.searchUsers.and.returnValue(
      of([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]),
    );

    service.searchUsers('test').subscribe((res) => {
      expect(userInformationServiceMock.searchUsers).toHaveBeenCalledOnceWith('test');
      expect(res).toEqual([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]);
      done();
    });
  });

  it('test hasUserPermissions false', (done) => {
    userAdministrationServiceMock.getUser.and.callFake((userId: string) =>
      of({ sbbUserId: userId }),
    );
    const hasUserPermissions = service.hasUserPermissions('u123456');
    hasUserPermissions.subscribe((val) => {
      expect(val).toBe(false);
      expect(userAdministrationServiceMock.getUser).toHaveBeenCalledOnceWith('u123456');
      done();
    });
  });

  it('test hasUserPermissions true', (done) => {
    userAdministrationServiceMock.getUser.and.callFake((userId: string) =>
      of({
        sbbUserId: userId,
        permissions: [
          {
            application: 'TTFN',
            role: 'WRITER',
            permissionRestrictions: [],
          },
        ],
      } as UserPermissionCreate),
    );
    const hasUserPermissions = service.hasUserPermissions('u123456');
    hasUserPermissions.subscribe((val) => {
      expect(userAdministrationServiceMock.getUser).toHaveBeenCalledOnceWith('u123456');
      expect(val).toBe(true);
      done();
    });
  });

  it('test getPermissionsFromUserModelAsArray', () => {
    let permissions = service.getPermissionsFromUserModelAsArray({});
    expect(permissions).toEqual([]);
    permissions = service.getPermissionsFromUserModelAsArray({
      permissions: new Set([
        {
          application: 'TTFN',
          role: 'WRITER',
          permissionRestrictions: [],
        },
      ]),
    });
    expect(permissions).toEqual([
      {
        application: 'TTFN',
        role: 'WRITER',
        permissionRestrictions: [],
      },
    ]);
  });

  it('test createUserPermission', (done) => {
    userAdministrationServiceMock.createUserPermission = jasmine.createSpy().and.returnValue(
      of({
        sbbUserId: 'u123456',
      } as User),
    );
    const createPermissionResult = service.createUserPermission({
      sbbUserId: 'u123456',
      permissions: [],
    });
    createPermissionResult.subscribe((val) => {
      expect(userAdministrationServiceMock.createUserPermission).toHaveBeenCalledOnceWith({
        sbbUserId: 'u123456',
        permissions: [],
      });
      expect(val).toEqual({
        sbbUserId: 'u123456',
      });
      done();
    });
  });
});
