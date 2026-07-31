import { describe, expect, it } from 'vitest';
import { UserMailHelper } from './user-mail.helper';
import { User } from '../../api';

describe('UserMailHelper', () => {
  it('should return the manual mail when it is set', () => {
    const user = { mail: 'azure@sbb.ch', manualMail: 'manual@sbb.ch' } as User;

    expect(UserMailHelper.effectiveMail(user)).toBe('manual@sbb.ch');
  });

  it('should return the azure mail when manual mail is undefined', () => {
    const user = { mail: 'azure@sbb.ch' } as User;

    expect(UserMailHelper.effectiveMail(user)).toBe('azure@sbb.ch');
  });

  it('should return the azure mail when manual mail is blank', () => {
    const user = { mail: 'azure@sbb.ch', manualMail: '   ' } as User;

    expect(UserMailHelper.effectiveMail(user)).toBe('azure@sbb.ch');
  });

  it('should return undefined when neither mail is set', () => {
    const user = {} as User;

    expect(UserMailHelper.effectiveMail(user)).toBeUndefined();
  });

  it('should return undefined when user itself is undefined', () => {
    expect(UserMailHelper.effectiveMail(undefined)).toBeUndefined();
  });
});
