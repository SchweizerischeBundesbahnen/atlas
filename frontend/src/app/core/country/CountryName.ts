import { Country } from '../../api';

export interface CountryName {
  short: string;
  uicCode?: number;
  enumCountry?: Country;
  path: string;
}
