import { TestBed } from '@angular/core/testing';
import { CMYK_COLOR_REGEX, ColorService, RGB_HEX_COLOR_REGEX } from './color.service';

describe('ColorService', () => {
  let service: ColorService;

  beforeEach(() => {
    service = TestBed.inject(ColorService);
  });

  it('should convert purplish cmyk to rgb hex string', () => {
    const hex = service.cmykToHex(29, 43, 0, 26);
    expect(hex).toBe('#866cbd');
  });

  it('should convert greenish cmyk to rgb hex string', () => {
    const hex = service.cmykToHex(100, 0, 45, 26);
    expect(hex).toBe('#00bd68');
  });

  it('should match string as hex color', () => {
    expect(RGB_HEX_COLOR_REGEX.test('#FFFFFF')).toBeTrue();
    expect(RGB_HEX_COLOR_REGEX.test('#0F0F0F')).toBeTrue();
    expect(RGB_HEX_COLOR_REGEX.test('#ab98cf')).toBeTrue();
  });

  it('should not match string as hex color', () => {
    expect(RGB_HEX_COLOR_REGEX.test('#FFFFFF0')).toBeFalse();
    expect(RGB_HEX_COLOR_REGEX.test('#0F0F0')).toBeFalse();
    expect(RGB_HEX_COLOR_REGEX.test('#ab98cfa')).toBeFalse();
  });

  it('should match string as cmyk color', () => {
    expect(CMYK_COLOR_REGEX.test('10,0,100,50')).toBeTrue();
    expect(CMYK_COLOR_REGEX.test('100,20,10,0')).toBeTrue();
    expect(CMYK_COLOR_REGEX.test('100,69,99,8')).toBeTrue();
  });

  it('should not match string as cmyk color', () => {
    expect(CMYK_COLOR_REGEX.test('(10,0,100,50)')).toBeFalse();
    expect(CMYK_COLOR_REGEX.test('101,20,10,0')).toBeFalse();
    expect(CMYK_COLOR_REGEX.test('200,20,10,0')).toBeFalse();
  });
});
