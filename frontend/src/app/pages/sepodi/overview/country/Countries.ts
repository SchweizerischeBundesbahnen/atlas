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
    { short: 'CU', enumCountry: Country.Cuba, path: 'cu' },
    { short: 'DK', enumCountry: Country.Denmark, path: 'dk' },
    { short: 'EG', enumCountry: Country.Egypt, path: 'eg' },
    { short: 'EE', enumCountry: Country.Estonia, path: 'ee' },
    { short: 'FI', enumCountry: Country.Finland, path: 'fi' },
    { short: 'FR', enumCountry: Country.France, path: 'fr' },
    { short: 'GE', enumCountry: Country.Georgia, path: 'ge' },
    { short: 'DE', enumCountry: Country.Germany, path: 'de' },
    { short: 'JP', enumCountry: Country.Japan, path: 'jp' },
    { short: 'GR', enumCountry: Country.Greece, path: 'gr' },
    { short: 'IR', enumCountry: Country.Iran, path: 'ir' },
    { short: 'IQ', enumCountry: Country.Iraq, path: 'iq' },
    { short: 'IE', enumCountry: Country.Ireland, path: 'ie' },
    { short: 'IL', enumCountry: Country.Israel, path: 'il' },
    { short: 'IT', enumCountry: Country.Italy, path: 'it' },
    { short: 'KZ', enumCountry: Country.Kazakhstan, path: 'kz' },
    { short: 'KG', enumCountry: Country.Kyrgyzstan, path: 'kg' },
    { short: 'LV', enumCountry: Country.Latvia, path: 'lv' },
    { short: 'LB', enumCountry: Country.Lebanon, path: 'lb' },
    { short: 'LT', enumCountry: Country.Lithuania, path: 'lt' },
    { short: 'LU', enumCountry: Country.Luxembourg, path: 'lu' },
    { short: 'MK', enumCountry: Country.Macedonia, path: 'mk' },
    { short: 'MA', enumCountry: Country.Morocco, path: 'ma' },
    { short: 'MD', enumCountry: Country.Moldova, path: 'md' },
    { short: 'MN', enumCountry: Country.Mongolia, path: 'mn' },
    { short: 'ME', enumCountry: Country.Montenegro, path: 'me' },
    { short: 'NO', enumCountry: Country.Norway, path: 'no' },
    { short: 'NL', enumCountry: Country.Netherlands, path: 'nl' },
    { short: 'PL', enumCountry: Country.Poland, path: 'pl' },
    { short: 'PT', enumCountry: Country.Portugal, path: 'pt' },
    { short: 'GB', enumCountry: Country.NorthernIreland, path: 'gb' },
    { short: 'CD', enumCountry: Country.Congo, path: 'cd' },
    { short: 'CZ', enumCountry: Country.CzechRepublic, path: 'cz' },
    { short: 'RO', enumCountry: Country.Romania, path: 'ro' },
    { short: 'RU', enumCountry: Country.Russia, path: 'ru' },
    { short: 'RS', enumCountry: Country.Serbia, path: 'rs' },
    { short: 'SY', enumCountry: Country.Syria, path: 'sy' },
    { short: 'SK', enumCountry: Country.Slovakia, path: 'sk' },
    { short: 'SI', enumCountry: Country.Slovenia, path: 'si' },
    { short: 'ES', enumCountry: Country.Spain, path: 'es' },
    { short: 'US', enumCountry: Country.UnitedStates, path: 'us' },
    { short: 'ZA', enumCountry: Country.SouthAfrica, path: 'za' },
    { short: 'SE', enumCountry: Country.Sweden, path: 'se' },
    { short: 'CH', enumCountry: Country.Switzerland, path: 'ch' },
    { short: 'TJ', enumCountry: Country.Tajikistan, path: 'tj' },
    { short: 'TN', enumCountry: Country.Tunisia, path: 'tn' },
    { short: 'TR', enumCountry: Country.Turkey, path: 'tr' },
    { short: 'TM', enumCountry: Country.Turkmenistan, path: 'tm' },
    { short: 'UA', enumCountry: Country.Ukraine, path: 'ua' },
    { short: 'HU', enumCountry: Country.Hungary, path: 'hu' },
    { short: 'UZ', enumCountry: Country.Uzbekistan, path: 'uz' },
    { short: 'VN', enumCountry: Country.Vietnam, path: 'vn' },
    { short: 'DE', enumCountry: Country.GermanyBus, path: 'de' },
    { short: 'AT', enumCountry: Country.AustriaBus, path: 'at' },
    { short: 'IT', enumCountry: Country.ItalyBus, path: 'it' },
    { short: 'FR', enumCountry: Country.FranceBus, path: 'fr' },
    { short: 'GYSEV/ROEE', enumCountry: Country.AustriaHungary, path: 'gysev/roee' },
    { short: 'ZBH', enumCountry: Country.BosniaAndHerzegovinaRailway, path: 'zbh' },
    { short: 'AF', enumCountry: Country.Afghanistan, path: 'af' },
    { short: 'AL', enumCountry: Country.Albania, path: 'al' },
    { short: 'DZ', enumCountry: Country.Algeria, path: 'dz' },
    { short: 'AM', enumCountry: Country.Armenia, path: 'am' },
    { short: 'AU', enumCountry: Country.Australia, path: 'au' },
    { short: 'AT', enumCountry: Country.Austria, path: 'at' },
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
