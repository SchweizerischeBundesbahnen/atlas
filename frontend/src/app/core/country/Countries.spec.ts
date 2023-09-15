import { Countries } from './Countries';
import { Country } from '../../api';

describe('Countries', () => {
  it('should get country by uicCode', () => {
    expect(Countries.fromUicCode(85).enumCountry).toBe(Country.Switzerland);
  });
});
