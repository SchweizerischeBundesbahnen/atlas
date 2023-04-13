import { isEmpty } from './strings';

describe('Strings Util Test', () => {
  it('isEmpty: should return true on empty string', () => {
    expect(isEmpty('')).toBeTrue();
  });

  it('isEmpty: should return false on not empty string', () => {
    expect(isEmpty('test')).toBeFalse();
  });
});
