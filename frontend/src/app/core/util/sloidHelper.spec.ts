import { SloidHelper } from './sloidHelper';

describe('SloidHelper', () => {
  it('should calculate ServicePointNumber correctly', () => {
    const number = SloidHelper.servicePointSloidToNumber('ch:1:sloid:7000');
    expect(number).toBe(8507000);
  });

  it('should throw error if sloid is not of service point', () => {
    expect(() => SloidHelper.servicePointSloidToNumber('ch:1:sloid:7000:0')).toThrow(
      new Error('Was not servicePoint sloid:' + ' ch:1:sloid:7000:0'),
    );
  });
});
