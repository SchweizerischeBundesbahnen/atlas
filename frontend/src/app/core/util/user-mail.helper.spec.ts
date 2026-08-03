import { describe, expect, it } from 'vitest';
import { UserMailHelper } from './user-mail.helper';
import { User } from '../../api';

const buildUser = (mail?: string, manualMailOverride?: string): User => {
  return {
    accountStatus: undefined,
    displayName: undefined,
    firstName: undefined,
    lastName: undefined,
    permissions: new Set(),
    sbbUserId: '',
    userId: undefined,
    mail,
    manualMailOverride,
  };
};

describe('UserMailHelper', () => {
  it('should return the manual mail when it is set', () => {
    const user = buildUser('azure@sbb.ch', 'manual@sbb.ch');

    expect(UserMailHelper.effectiveMail(user)).toBe('manual@sbb.ch');
  });

  it('should return the azure mail when manual mail is undefined', () => {
    const user: User = buildUser('azure@sbb.ch');

    expect(UserMailHelper.effectiveMail(user)).toBe('azure@sbb.ch');
  });

  it('should return the azure mail when manual mail is blank', () => {
    const user: User = buildUser('azure@sbb.ch', '   ');

    expect(UserMailHelper.effectiveMail(user)).toBe('azure@sbb.ch');
  });

  it('should return undefined when neither mail is set', () => {
    const user: User = buildUser();

    expect(UserMailHelper.effectiveMail(user)).toBeUndefined();
  });

  it('should return undefined when user itself is undefined', () => {
    expect(UserMailHelper.effectiveMail(undefined)).toBeUndefined();
  });
});
