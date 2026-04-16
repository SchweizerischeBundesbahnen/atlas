import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { UserAdministrationUserDetailResolver } from './user-administration-user-detail-resolver.service';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { Permission } from '../../../../api';

describe('UserAdministrationUserDetailResolver', () => {
  let resolver: UserAdministrationUserDetailResolver;
  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'getUser'>>;

  beforeEach(() => {
    userAdministrationService = {
      getUser: vi.fn(),
    };
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
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
    expect(userAdministrationService.getUser).not.toHaveBeenCalled();
    expect(userModel).toBeUndefined();
  });

  it('test sbbUserIdParam=userId', async () => {
    const routeMock = {
      paramMap: convertToParamMap({ sbbUserId: 'userId' }),
    } as ActivatedRouteSnapshot;

    userAdministrationService.getUser.mockReturnValue(
      of({
        sbbUserId: 'userId',
        permissions: new Set<Permission>(),
      })
    );
    const userModel = await firstValueFrom(resolver.resolve(routeMock));
    expect(userAdministrationService.getUser).toHaveBeenCalledExactlyOnceWith('userId');
    expect(userModel?.sbbUserId).toBe('userId');
  });
});
