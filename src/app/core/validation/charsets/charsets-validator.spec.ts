import { FormControl } from '@angular/forms';
import { CharsetsValidator } from './charsets-validator';

describe('Charsets Validator', () => {
  it('should allow numbers and dots', () => {
    const numericWithDot = CharsetsValidator.numericWithDot;

    expect(numericWithDot(new FormControl('0'))).toBeNull();
    expect(numericWithDot(new FormControl('0.9'))).toBeNull();

    expect(numericWithDot(new FormControl('0.x9'))).toBeDefined();
    expect(numericWithDot(new FormControl('a'))).toBeDefined();
  });

  it('should allow SID4PT charset', () => {
    const sid4pt = CharsetsValidator.sid4pt;

    expect(sid4pt(new FormControl('aE2._:78-B'))).toBeNull();
    expect(sid4pt(new FormControl('duper.-:_234'))).toBeNull();

    expect(sid4pt(new FormControl('aser%'))).toBeDefined();
    expect(sid4pt(new FormControl('&'))).toBeDefined();
    expect(sid4pt(new FormControl('/'))).toBeDefined();
  });

  it('should allow ISO-8859-1 charset', () => {
    const iso88591 = CharsetsValidator.iso88591;

    expect(iso88591(new FormControl('abcÂÃ'))).toBeNull();
    expect(iso88591(new FormControl('abc'))).toBeNull();
    expect(iso88591(new FormControl('àáâãäåçèéêëìíîðñòôõöö'))).toBeNull();

    expect(iso88591(new FormControl('a 你 好'))).toBeDefined();
    expect(iso88591(new FormControl('\uD83D\uDE00\uD83D\uDE01\uD83D'))).toBeDefined();
    expect(iso88591(new FormControl('╗'))).toBeDefined();
  });
});
