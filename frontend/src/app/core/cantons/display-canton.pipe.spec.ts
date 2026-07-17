import { TransformCantonToShorthandPipe } from './display-canton.pipe';
import { SwissCanton } from '../../api';
import { describe, expect, it } from 'vitest';

describe('TransformCantonToShorthandPipe', () => {
  const pipe = new TransformCantonToShorthandPipe();

  it('returns the i18n key for a canton', () => {
    expect(pipe.transform(SwissCanton.Bern)).toBe('TTH.CANTON.BE');
  });

  it('returns "-" for a missing value', () => {
    expect(pipe.transform()).toBe('-');
  });
});
