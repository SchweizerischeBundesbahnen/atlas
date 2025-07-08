import { TestBed } from '@angular/core/testing';

import { UserAdministrationUserDetailResolver } from './user-administration-user-detail-resolver.service';
import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  RouterModule,
} from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import SpyObj = jasmine.SpyObj;
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { Permission } from '../../../../api';

describe('UserAdministrationUserDetailResolver', () => {
  let resolver: UserAdministrationUserDetailResolver;

  let userAdministrationServiceSpy: SpyObj<UserAdministrationService>;

  beforeEach(() => {
    userAdministrationServiceSpy = jasmine.createSpyObj(
      'UserAdministrationService',
      ['getUser']
    );
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(UserAdministrationUserDetailResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('test sbbUserIdParam=add', async () => {
    const routeMock = {
      paramMap: convertToParamMap({ sbbUserId: 'add' }),
    } as ActivatedRouteSnapshot;

    const userModel = await firstValueFrom(resolver.resolve(routeMock));
    expect(userAdministrationServiceSpy.getUser).not.toHaveBeenCalled();
    expect(userModel).toBeUndefined();
  });

  it('test sbbUserIdParam=userId', async () => {
    const routeMock = {
      paramMap: convertToParamMap({ sbbUserId: 'userId' }),
    } as ActivatedRouteSnapshot;

    userAdministrationServiceSpy.getUser.and.returnValue(
      of({
        sbbUserId: 'userId',
        permissions: new Set<Permission>(),
      })
    );
    const userModel = await firstValueFrom(resolver.resolve(routeMock));
    expect(userAdministrationServiceSpy.getUser).toHaveBeenCalledOnceWith(
      'userId'
    );
    expect(userModel).toEqual({
      sbbUserId: 'userId',
      permissions: new Set<Permission>(),
    });
  });
});
