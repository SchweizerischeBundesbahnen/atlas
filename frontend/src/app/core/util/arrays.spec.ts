import { describe, expect, it } from 'vitest';
import { addElementsToArrayWhenNotUndefined, toNumberArrayStrict } from './arrays';

describe('Arrays Util Test', () => {
  it('addElementsToArrayWhenNotUndefined: should not add undefined elements to array', () => {
    expect(addElementsToArrayWhenNotUndefined(undefined, 'one', undefined, 'two', 'three', undefined)).toEqual([
      'one',
      'two',
      'three',
    ]);
  });

  it('toNumberArrayStrict: should map undefined to empty array', () => {
    const result = toNumberArrayStrict(undefined);
    expect(result).toEqual([]);
  });

  it('toNumberArrayStrict: should map string to number array', () => {
    const result = toNumberArrayStrict('12');
    expect(result).toEqual([12]);
  });

  it('toNumberArrayStrict: should map string array to number array', () => {
    const result = toNumberArrayStrict(['1', '2']);
    expect(result).toEqual([1, 2]);
  });
});
