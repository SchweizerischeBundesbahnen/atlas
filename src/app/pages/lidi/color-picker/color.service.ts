import { Injectable } from '@angular/core';

export const RGB_HEX_COLOR_REGEX = /^#([a-fA-F0-9]{6})$/i;
export const CMYK_COLOR_REGEX = /^(([0-9][0-9]?|100),){3}([0-9][0-9]?|100)$/i;

@Injectable({ providedIn: 'root' })
export class ColorService {
  cmykToHex(c: number, m: number, y: number, k: number) {
    c = c / 100;
    m = m / 100;
    y = y / 100;
    k = k / 100;

    let r = 1 - (c * (1 - k) + k);
    let g = 1 - (m * (1 - k) + k);
    let b = 1 - (y * (1 - k) + k);

    r = Math.round(255 * r);
    g = Math.round(255 * g);
    b = Math.round(255 * b);

    return this.rgbToHex(r, g, b);
  }

  rgbToHex(r: number, g: number, b: number) {
    return (
      '#' +
      ColorService.toTwoDigitHex(r) +
      ColorService.toTwoDigitHex(g) +
      ColorService.toTwoDigitHex(b)
    );
  }

  private static toTwoDigitHex(value: number): string {
    const hex = value.toString(16);
    return hex.length == 1 ? '0' + hex : hex;
  }
}
