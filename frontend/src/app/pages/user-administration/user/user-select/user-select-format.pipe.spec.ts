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

  it('should display mail as provided by the backend (already the effective value)', () => {
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
        mail: 'manual@sbb.ch',
        originalMail: 'azure@sbb.ch',
      })
    ).toBe('Test User (manual@sbb.ch)');
  });

  it('should display the mail when no override is active', () => {
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
        mail: 'azure@sbb.ch',
        originalMail: 'azure@sbb.ch',
      })
    ).toBe('Test User (azure@sbb.ch)');
  });
});
