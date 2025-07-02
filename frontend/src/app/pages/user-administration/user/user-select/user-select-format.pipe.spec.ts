import { UserSelectFormatPipe } from './user-select-format.pipe';
import { Permission } from '../../../../api';

describe('UserSelectFormatPipe', () => {
  it('format user', () => {
    const pipe = new UserSelectFormatPipe();
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
    const pipe = new UserSelectFormatPipe();
    expect(
      pipe.transform({
        sbbUserId: 'uid',
        permissions: new Set<Permission>(),
        displayName: 'Test User',
      })
    ).toBe('Test User ');
  });
});
