import { FormControl } from '@angular/forms';
import { AtlasCharsetsValidator } from './atlas-charsets-validator';

describe('Atlas Charsets Validator', () => {
  it('should allow numbers and dots', () => {
    const numericWithDot = AtlasCharsetsValidator.numericWithDot;

    expect(numericWithDot(new FormControl('0'))).toBeNull();
    expect(numericWithDot(new FormControl('0.9'))).toBeNull();

    expect(numericWithDot(new FormControl('0.x9'))).toBeDefined();
    expect(numericWithDot(new FormControl('a'))).toBeDefined();
  });

  it('should allow SID4PT charset', () => {
    const sid4pt = AtlasCharsetsValidator.sid4pt;

    expect(sid4pt(new FormControl('aE2._:78-B'))).toBeNull();
    expect(sid4pt(new FormControl('duper.-:_234'))).toBeNull();

    expect(sid4pt(new FormControl('aser%'))).toBeDefined();
    expect(sid4pt(new FormControl('&'))).toBeDefined();
    expect(sid4pt(new FormControl('/'))).toBeDefined();
  });

  it('should allow ISO-8859-1 charset', () => {
    const iso88591 = AtlasCharsetsValidator.iso88591;

    expect(iso88591(new FormControl('abcÂÃ'))).toBeNull();
    expect(iso88591(new FormControl('abc'))).toBeNull();
    expect(iso88591(new FormControl('àáâãäåçèéêëìíîðñòôõöö'))).toBeNull();

    expect(iso88591(new FormControl('a 你 好'))).toBeDefined();
    expect(iso88591(new FormControl('\uD83D\uDE00\uD83D\uDE01\uD83D'))).toBeDefined();
    expect(iso88591(new FormControl('╗'))).toBeDefined();
  });

  it('should allow alphaNumeric charset', () => {
    const alphaNumeric = AtlasCharsetsValidator.alphaNumeric;

    expect(alphaNumeric(new FormControl('asdf'))).toBeNull();
    expect(alphaNumeric(new FormControl('sbb01'))).toBeNull();
    expect(alphaNumeric(new FormControl('AAHS2S'))).toBeNull();

    expect(alphaNumeric(new FormControl('asdf 2'))).toBeDefined();
    expect(alphaNumeric(new FormControl('.hello.'))).toBeDefined();
    expect(alphaNumeric(new FormControl('╗'))).toBeDefined();
  });

  it('should allow ISO-8859-1 charset', () => {
    const email = AtlasCharsetsValidator.email;

    expect(email(new FormControl('this@here.com'))).toBeNull();
    expect(email(new FormControl('test@sbb.ch'))).toBeNull();
    expect(email(new FormControl('Dude@sbb.ch'))).toBeNull();

    expect(email(new FormControl('a@'))).toBeDefined();
    expect(email(new FormControl('@sbb.ch'))).toBeDefined();
    expect(email(new FormControl('this@.ch'))).toBeDefined();
    expect(email(new FormControl(' @ .  '))).toBeDefined();
    expect(email(new FormControl('@@.@@'))).toBeDefined();
  });

  it('should allow Decimal with max digits', () => {
    const decimalWithDigits = AtlasCharsetsValidator.decimalWithDigits(3);

    expect(decimalWithDigits(new FormControl('123'))).toBeNull();
    expect(decimalWithDigits(new FormControl('1.002'))).toBeNull();
    expect(decimalWithDigits(new FormControl('500.0'))).toBeNull();

    expect(decimalWithDigits(new FormControl('a'))).toBeDefined();
    expect(decimalWithDigits(new FormControl('1.0002'))).toBeDefined();
  });

  it('should allow sid4pt with 1 colon', () => {
    const colonSeperatedSid4pt = AtlasCharsetsValidator.colonSeperatedSid4pt(1);

    // Valid
    expect(colonSeperatedSid4pt(new FormControl('1:002'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl('5:000'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl('a:.'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl('.:.'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl(':.'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl(':a'))).toBeNull();

    // Not Valid
    expect(colonSeperatedSid4pt(new FormControl('a'))).toBeDefined();
    expect(colonSeperatedSid4pt(new FormControl('a:'))).toBeDefined();
    expect(colonSeperatedSid4pt(new FormControl(':'))).toBeDefined();
    expect(colonSeperatedSid4pt(new FormControl('::'))).toBeDefined();
    expect(colonSeperatedSid4pt(new FormControl('1:00:a'))).toBeDefined();
  });

  it('should allow only sid4pt without colon or other chars', () => {
    const colonSeperatedSid4pt = AtlasCharsetsValidator.colonSeperatedSid4pt(0);

    expect(colonSeperatedSid4pt(new FormControl('002'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl('000'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl('a'))).toBeNull();
    expect(colonSeperatedSid4pt(new FormControl('.'))).toBeNull();

    expect(colonSeperatedSid4pt(new FormControl(':a'))).toBeDefined();
    expect(colonSeperatedSid4pt(new FormControl(':'))).toBeDefined();
    expect(colonSeperatedSid4pt(new FormControl('1:00:02'))).toBeDefined();
  });

  it('should allow upper case alphanumerics', () => {
    const uppercaseNumeric = AtlasCharsetsValidator.uppercaseNumeric;

    expect(uppercaseNumeric(new FormControl('W'))).toBeNull();
    expect(uppercaseNumeric(new FormControl('BN'))).toBeNull();

    expect(uppercaseNumeric(new FormControl('a'))).toBeDefined();
    expect(uppercaseNumeric(new FormControl(':'))).toBeDefined();
    expect(uppercaseNumeric(new FormControl('GGG.'))).toBeDefined();
    expect(uppercaseNumeric(new FormControl('BAU,'))).toBeDefined();
  });
});
