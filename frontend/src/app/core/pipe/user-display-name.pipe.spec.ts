import { beforeEach, describe, expect, it, Mocked, vi } from 'vitest';
import { UserDisplayNamePipe } from './user-display-name.pipe';
import { firstValueFrom, of } from 'rxjs';
import { UserAdministrationService } from '../../api/service/user-administration/user-administration.service';
import { TestBed } from '@angular/core/testing';

describe('UserDisplayNamePipe', () => {
  let pipe: UserDisplayNamePipe;

  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'getUserDisplayName'>>;

  beforeEach(() => {
    userAdministrationService = {
      getUserDisplayName: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        UserDisplayNamePipe,
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
      ],
    });

    pipe = TestBed.inject(UserDisplayNamePipe);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('empty observable if userId undefined', async () => {
    await firstValueFrom(pipe.transform());
    expect(userAdministrationService.getUserDisplayName).not.toHaveBeenCalled();
  });

  it('should return displayName over service', async () => {
    userAdministrationService.getUserDisplayName.mockReturnValue(of({ displayName: 'Atlas User' }));
    const result = await firstValueFrom(pipe.transform('u123456'));
    expect(result).toEqual('Atlas User');
  });
});
