import { CountryName } from './CountryName';
import { Country } from '../../../../api';

export class Countries {
  public static countryNames: CountryName[] = [
    { short: 'AZ', enumCountry: Country.Azerbaijan, path: 'az' },
    { short: 'BE', enumCountry: Country.Belgium, path: 'ag' },
    { short: 'BY', enumCountry: Country.Belarus, path: 'ag' },
    { short: 'BA', enumCountry: Country.SerbBosniaAndHerzegovina, path: 'ba' },
    { short: 'BA', enumCountry: Country.BosniaAndHerzegovina, path: 'ba' },
    { short: 'BA', enumCountry: Country.CroatBosniaAndHerzegovina, path: 'ba' },
    { short: 'BG', enumCountry: Country.Bulgaria, path: 'bg' },
    { short: 'CA', enumCountry: Country.Canada, path: 'ca' },
    { short: 'CN', enumCountry: Country.China, path: 'cn' },
    { short: 'KP', enumCountry: Country.NorthKorea, path: 'kp' },
    { short: 'KR', enumCountry: Country.SouthKorea, path: 'kr' },
    { short: 'HR', enumCountry: Country.Croatia, path: 'hr' },
  ];

  public static swiss: CountryName = {
    short: 'AZ',
    path: 'az',
    enumCountry: 'AZERBAIJAN' as Country,
  };

  public static allCountries: CountryName[] = [Countries.swiss].concat(Countries.countryNames);

  public static getCountryEnum(canton: string | null): Country | undefined {
    if (!canton) {
      return undefined;
    }
    const foundCountry = Countries.allCountries.find((c) => {
      return c.short.toLowerCase() === canton?.toLowerCase();
    });
    if (!foundCountry) {
      return undefined;
    }
    return foundCountry.enumCountry;
  }

  public static fromCountry(country: Country | undefined): CountryName | undefined {
    return this.countryNames.find((countryName) => countryName.enumCountry === country);
  }
}
