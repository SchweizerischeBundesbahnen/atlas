import { SplitServicePointNumberPipe } from './split-service-point-number.pipe';
import { ServicePointNumber } from '../../api';

describe('SplitServicePointNumberPipe', () => {
  it('create an instance', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe).toBeTruthy();
  });

  it('should split without highlight', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe.transform('8507000')).toBe('85 07000');
  });

  it('should split with highlight', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe.transform('850<b>7000</b>')).toBe('85 0<b>7000</b>');
  });

  it('should split at beginning with highlight', () => {
    const pipe = new SplitServicePointNumberPipe();
    expect(pipe.transform('<b>85</b>07000')).toBe('<b>85 </b>07000');
  });

  it('should split servicePointNumber', () => {
    const pipe = new SplitServicePointNumberPipe();
    const servicePointNumber: ServicePointNumber = {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    };
    expect(pipe.transform(servicePointNumber)).toBe('85 07000');
  });
});
