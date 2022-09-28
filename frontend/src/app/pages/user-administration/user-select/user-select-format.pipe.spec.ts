import { UserSelectFormatPipe } from './user-select-format.pipe';

describe('UserSelectFormatPipe', () => {
  it('create an instance', () => {
    const pipe = new UserSelectFormatPipe();
    expect(pipe).toBeTruthy();
    expect(
      pipe.transform({
        displayName: 'Test User',
        mail: 'test.user@sbb.ch',
      })
    ).toBe('Test User (test.user@sbb.ch)');
  });
});
