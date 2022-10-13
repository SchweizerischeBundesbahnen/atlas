import { TestBed } from '@angular/core/testing';

import { UserAdministrationResolver } from './user-administration.resolver';
import SpyObj = jasmine.SpyObj;
import { UserService } from '../service/user.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRouteSnapshot, ParamMap } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';

describe('UserAdministrationResolver', () => {
  let resolver: UserAdministrationResolver;

  let userServiceSpy: SpyObj<UserService>;

  beforeEach(() => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUser']);
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        {
          provide: UserService,
          useValue: userServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(UserAdministrationResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('test sbbUserIdParam=add', async () => {
    const routeMock: ActivatedRouteSnapshot = {
      get paramMap(): ParamMap {
        return {
          get(name): string | null {
            return 'add';
          },
        } as ParamMap;
      },
    } as ActivatedRouteSnapshot;

    const userModel = await firstValueFrom(resolver.resolve(routeMock));
    expect(userServiceSpy.getUser).not.toHaveBeenCalled();
    expect(userModel).toEqual({});
  });

  it('test sbbUserIdParam=userId', async () => {
    const routeMock: ActivatedRouteSnapshot = {
      get paramMap(): ParamMap {
        return {
          get(name): string | null {
            return 'userId';
          },
        } as ParamMap;
      },
    } as ActivatedRouteSnapshot;

    userServiceSpy.getUser.and.returnValue(
      of({
        sbbUserId: 'userId',
      })
    );
    const userModel = await firstValueFrom(resolver.resolve(routeMock));
    expect(userServiceSpy.getUser).toHaveBeenCalledOnceWith('userId');
    expect(userModel).toEqual({
      sbbUserId: 'userId',
    });
  });
});
