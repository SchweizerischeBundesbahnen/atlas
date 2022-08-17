import { TestBed } from '@angular/core/testing';

import { UserService } from './user.service';
import {
  AtlasGraphApiService,
  ContainerString,
  LiDiUserAdministrationService,
  UserModel,
  UserPermission,
} from '../../../api';
import { of } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  let liDiUserAdministrationServiceMock: LiDiUserAdministrationServiceMock;
  let atlasGraphApiServiceMock: AtlasGraphApiServiceMock;

  class LiDiUserAdministrationServiceMock {
    getUsers: any = undefined;
    getUserPermissions: any = undefined;
  }

  class AtlasGraphApiServiceMock {
    resolveUsers: any = undefined;
    searchUsers: any = undefined;
  }

  beforeEach(() => {
    liDiUserAdministrationServiceMock = new LiDiUserAdministrationServiceMock();
    atlasGraphApiServiceMock = new AtlasGraphApiServiceMock();
    TestBed.configureTestingModule({
      providers: [
        {
          provide: LiDiUserAdministrationService,
          useValue: liDiUserAdministrationServiceMock,
        },
        {
          provide: AtlasGraphApiService,
          useValue: atlasGraphApiServiceMock,
        },
      ],
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('test getUsers', (done) => {
    liDiUserAdministrationServiceMock.getUsers = jasmine
      .createSpy()
      .and.returnValue(of<ContainerString>({ totalCount: 5, objects: ['u123456', 'u654321'] }));
    atlasGraphApiServiceMock.resolveUsers = jasmine
      .createSpy()
      .and.callFake((userIds: string[]) =>
        of(userIds.map((id) => ({ sbbUserId: id } as UserModel)))
      );

    service.getUsers(10, 10).subscribe((res) => {
      expect(res).toEqual({
        users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }],
        totalCount: 5,
      });
      done();
    });
  });

  it('test getUserPermissions', (done) => {
    liDiUserAdministrationServiceMock.getUserPermissions = jasmine
      .createSpy()
      .and.returnValue(of([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]));

    service.getUserPermissions('u123456').subscribe((res) => {
      expect(liDiUserAdministrationServiceMock.getUserPermissions).toHaveBeenCalledOnceWith(
        'u123456'
      );
      expect(res).toEqual([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }] as UserPermission[]);
      done();
    });
  });

  it('test searchUsers', (done) => {
    atlasGraphApiServiceMock.searchUsers = jasmine
      .createSpy()
      .and.returnValue(of([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]));

    service.searchUsers('test').subscribe((res) => {
      expect(atlasGraphApiServiceMock.searchUsers).toHaveBeenCalledOnceWith('test');
      expect(res).toEqual([{ sbbUserId: 'u123456' }, { sbbUserId: 'u654321' }]);
      done();
    });
  });
});
