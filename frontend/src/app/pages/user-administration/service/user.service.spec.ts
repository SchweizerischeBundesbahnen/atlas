import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';
import {
  ContainerUserModel,
  UserAdministrationService,
  UserInformationService,
  UserModel,
  UserPermissionCreateModel,
} from '../../../api';
import { of } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  let userAdministrationServiceMock: UserAdministrationServiceMock;
  let userInformationServiceMock: UserInformationServiceMock;

  class UserAdministrationServiceMock {
    getUsers: any = undefined;
    getUser: any = undefined;
    createUserPermission: any = undefined;
  }

  class UserInformationServiceMock {
    searchUsers: any = undefined;
  }

  beforeEach(() => {
    userAdministrationServiceMock = new UserAdministrationServiceMock();
    userInformationServiceMock = new UserInformationServiceMock();
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
      ],
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('test getUsers', (done) => {
    userAdministrationServiceMock.getUsers = jasmine.createSpy().and.returnValue(
      of<ContainerUserModel>({
        totalCount: 5,
        objects: [{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }],
      })
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
    userAdministrationServiceMock.getUser = jasmine
      .createSpy()
      .and.returnValue(of({ sbbUserId: 'u123456' }));

    service.getUser('u123456').subscribe((res) => {
      expect(userAdministrationServiceMock.getUser).toHaveBeenCalledOnceWith('u123456');
      expect(res).toEqual({ sbbUserId: 'u123456' });
      done();
    });
  });

  it('test searchUsers', (done) => {
    userInformationServiceMock.searchUsers = jasmine
      .createSpy()
      .and.returnValue(of([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]));

    service.searchUsers('test').subscribe((res) => {
      expect(userInformationServiceMock.searchUsers).toHaveBeenCalledOnceWith('test');
      expect(res).toEqual([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]);
      done();
    });
  });

  it('test hasUserPermissions false', (done) => {
    userAdministrationServiceMock.getUser = jasmine
      .createSpy()
      .and.callFake((userId) => of({ sbbUserId: userId }));
    const hasUserPermissions = service.hasUserPermissions('u123456');
    hasUserPermissions.subscribe((val) => {
      expect(val).toBe(false);
      expect(userAdministrationServiceMock.getUser).toHaveBeenCalledOnceWith('u123456');
      done();
    });
  });

  it('test hasUserPermissions true', (done) => {
    userAdministrationServiceMock.getUser = jasmine.createSpy().and.callFake((userId) =>
      of({
        sbbUserId: userId,
        permissions: [
          {
            application: 'TTFN',
            role: 'WRITER',
            sboids: [],
          },
        ],
      } as UserPermissionCreateModel)
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
          sboids: [],
        },
      ]),
    });
    expect(permissions).toEqual([
      {
        application: 'TTFN',
        role: 'WRITER',
        sboids: [],
      },
    ]);
  });

  it('test createUserPermission', (done) => {
    userAdministrationServiceMock.createUserPermission = jasmine.createSpy().and.returnValue(
      of({
        sbbUserId: 'u123456',
      } as UserModel)
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
