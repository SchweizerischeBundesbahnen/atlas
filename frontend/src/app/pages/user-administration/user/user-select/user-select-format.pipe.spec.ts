import { beforeEach, describe, expect, it } from 'vitest';
import { UserSelectFormatPipe } from './user-select-format.pipe';
import { Permission } from '../../../../api';

describe('UserSelectFormatPipe', () => {
  let pipe: UserSelectFormatPipe;

  beforeEach(() => {
    pipe = new UserSelectFormatPipe();
  });

  it('format user', () => {
    expect(pipe).toBeTruthy();
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
        mail: 'test.user@sbb.ch',
      })
    ).toBe('Test User (test.user@sbb.ch)');
  });

  it('format user without mail', () => {
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
      })
    ).toBe('Test User ');
  });

  it('should display the manual mail when the user has one', () => {
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
        mail: 'azure@sbb.ch',
        manualMail: 'manual@sbb.ch',
      })
    ).toBe('Test User (manual@sbb.ch)');
  });

  it('should display the azure mail when the user has no override', () => {
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
        mail: 'azure@sbb.ch',
      })
    ).toBe('Test User (azure@sbb.ch)');
  });
});
