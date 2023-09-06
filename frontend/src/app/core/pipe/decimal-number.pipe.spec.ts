import { DecimalNumberPipe } from './decimal-number.pipe';

describe('DecimalNumberPipe', () => {
  it('display decimal numbers nice and smooth', () => {
    const pipe = new DecimalNumberPipe();
    expect(pipe).toBeTruthy();
    expect(pipe.transform(12315.2, 5)).toBe('12315.20000');
  });
});
