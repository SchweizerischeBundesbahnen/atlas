import { addElementsToArrayWhenNotUndefined } from './arrays';

describe('Arrays Util Test', () => {
  it('addElementsToArrayWhenNotUndefined: should not add undefined elements to array', () => {
    expect(
      addElementsToArrayWhenNotUndefined(undefined, 'one', undefined, 'two', 'three', undefined)
    ).toEqual(['one', 'two', 'three']);
  });
});
