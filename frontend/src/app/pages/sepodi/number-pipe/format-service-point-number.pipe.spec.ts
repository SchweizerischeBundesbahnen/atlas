import { FormatServicePointNumber } from './format-service-point-number.pipe';
import { ServicePointNumber } from '../../../api';

describe('FormatServicePointNumber', () => {
  const pipe = new FormatServicePointNumber();

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display service point number formatted', () => {
    const servicePointNumber: ServicePointNumber = {
      number: 8507000,
      numberShort: 7000,
      checkDigit: 3,
      uicCountryCode: 85,
    };
    expect(pipe.transform(servicePointNumber)).toEqual('85 07000');
  });
});
