import { CountryName } from './CountryName';
import { Country } from '../../api';

export class Countries {
  public static readonly countryNames: CountryName[] = [
    { short: 'AZ', uicCode: 57, enumCountry: Country.Azerbaijan, path: 'az' },
    { short: 'BE', uicCode: 88, enumCountry: Country.Belgium, path: 'ag' },
    { short: 'BY', uicCode: 21, enumCountry: Country.Belarus, path: 'ag' },
    { short: 'BA', uicCode: 44, enumCountry: Country.SerbBosniaAndHerzegovina, path: 'ba' },
    { short: 'BA', uicCode: 49, enumCountry: Country.BosniaAndHerzegovina, path: 'ba' },
    { short: 'BA', uicCode: 50, enumCountry: Country.CroatBosniaAndHerzegovina, path: 'ba' },
    { short: 'BG', uicCode: 52, enumCountry: Country.Bulgaria, path: 'bg' },
    { short: 'CA', uicCode: 0, enumCountry: Country.Canada, path: 'ca' },
    { short: 'CN', uicCode: 33, enumCountry: Country.China, path: 'cn' },
    { short: 'KP', uicCode: 30, enumCountry: Country.NorthKorea, path: 'kp' },
    { short: 'KR', uicCode: 61, enumCountry: Country.SouthKorea, path: 'kr' },
    { short: 'HR', uicCode: 78, enumCountry: Country.Croatia, path: 'hr' },
    { short: 'CU', uicCode: 40, enumCountry: Country.Cuba, path: 'cu' },
    { short: 'DK', uicCode: 86, enumCountry: Country.Denmark, path: 'dk' },
    { short: 'EG', uicCode: 90, enumCountry: Country.Egypt, path: 'eg' },
    { short: 'EE', uicCode: 26, enumCountry: Country.Estonia, path: 'ee' },
    { short: 'FI', uicCode: 10, enumCountry: Country.Finland, path: 'fi' },
    { short: 'FR', uicCode: 87, enumCountry: Country.France, path: 'fr' },
    { short: 'GE', uicCode: 28, enumCountry: Country.Georgia, path: 'ge' },
    { short: 'DE', uicCode: 80, enumCountry: Country.Germany, path: 'de' },
    { short: 'JP', uicCode: 42, enumCountry: Country.Japan, path: 'jp' },
    { short: 'GR', uicCode: 73, enumCountry: Country.Greece, path: 'gr' },
    { short: 'IR', uicCode: 96, enumCountry: Country.Iran, path: 'ir' },
    { short: 'IQ', uicCode: 99, enumCountry: Country.Iraq, path: 'iq' },
    { short: 'IE', uicCode: 60, enumCountry: Country.Ireland, path: 'ie' },
    { short: 'IL', uicCode: 95, enumCountry: Country.Israel, path: 'il' },
    { short: 'IT', uicCode: 83, enumCountry: Country.Italy, path: 'it' },
    { short: 'KZ', uicCode: 27, enumCountry: Country.Kazakhstan, path: 'kz' },
    { short: 'KG', uicCode: 59, enumCountry: Country.Kyrgyzstan, path: 'kg' },
    { short: 'LV', uicCode: 25, enumCountry: Country.Latvia, path: 'lv' },
    { short: 'LB', uicCode: 98, enumCountry: Country.Lebanon, path: 'lb' },
    { short: 'LT', uicCode: 24, enumCountry: Country.Lithuania, path: 'lt' },
    { short: 'LU', uicCode: 82, enumCountry: Country.Luxembourg, path: 'lu' },
    { short: 'MK', uicCode: 65, enumCountry: Country.Macedonia, path: 'mk' },
    { short: 'MA', uicCode: 93, enumCountry: Country.Morocco, path: 'ma' },
    { short: 'MD', uicCode: 23, enumCountry: Country.Moldova, path: 'md' },
    { short: 'MN', uicCode: 31, enumCountry: Country.Mongolia, path: 'mn' },
    { short: 'ME', uicCode: 62, enumCountry: Country.Montenegro, path: 'me' },
    { short: 'NO', uicCode: 76, enumCountry: Country.Norway, path: 'no' },
    { short: 'NL', uicCode: 84, enumCountry: Country.Netherlands, path: 'nl' },
    { short: 'PL', uicCode: 51, enumCountry: Country.Poland, path: 'pl' },
    { short: 'PT', uicCode: 94, enumCountry: Country.Portugal, path: 'pt' },
    { short: 'GB', uicCode: 70, enumCountry: Country.GreatBritain, path: 'gb' },
    { short: 'CD', uicCode: 2, enumCountry: Country.Congo, path: 'cd' },
    { short: 'CZ', uicCode: 54, enumCountry: Country.CzechRepublic, path: 'cz' },
    { short: 'RO', uicCode: 53, enumCountry: Country.Romania, path: 'ro' },
    { short: 'RU', uicCode: 20, enumCountry: Country.Russia, path: 'ru' },
    { short: 'RS', uicCode: 72, enumCountry: Country.Serbia, path: 'rs' },
    { short: 'SY', uicCode: 97, enumCountry: Country.Syria, path: 'sy' },
    { short: 'SK', uicCode: 56, enumCountry: Country.Slovakia, path: 'sk' },
    { short: 'SI', uicCode: 79, enumCountry: Country.Slovenia, path: 'si' },
    { short: 'ES', uicCode: 71, enumCountry: Country.Spain, path: 'es' },
    { short: 'US', uicCode: 46, enumCountry: Country.UnitedStates, path: 'us' },
    { short: 'ZA', uicCode: 3, enumCountry: Country.SouthAfrica, path: 'za' },
    { short: 'SE', uicCode: 74, enumCountry: Country.Sweden, path: 'se' },
    { short: 'CH', uicCode: 85, enumCountry: Country.Switzerland, path: 'ch' },
    { short: 'TJ', uicCode: 66, enumCountry: Country.Tajikistan, path: 'tj' },
    { short: 'TN', uicCode: 91, enumCountry: Country.Tunisia, path: 'tn' },
    { short: 'TR', uicCode: 75, enumCountry: Country.Turkey, path: 'tr' },
    { short: 'TM', uicCode: 67, enumCountry: Country.Turkmenistan, path: 'tm' },
    { short: 'UA', uicCode: 22, enumCountry: Country.Ukraine, path: 'ua' },
    { short: 'HU', uicCode: 55, enumCountry: Country.Hungary, path: 'hu' },
    { short: 'UZ', uicCode: 29, enumCountry: Country.Uzbekistan, path: 'uz' },
    { short: 'VN', uicCode: 32, enumCountry: Country.Vietnam, path: 'vn' },
    { short: 'DE', uicCode: 11, enumCountry: Country.GermanyBus, path: 'de' },
    { short: 'AT', uicCode: 12, enumCountry: Country.AustriaBus, path: 'at' },
    { short: 'IT', uicCode: 13, enumCountry: Country.ItalyBus, path: 'it' },
    { short: 'FR', uicCode: 14, enumCountry: Country.FranceBus, path: 'fr' },
    { short: 'GYSEV/ROEE', uicCode: 43, enumCountry: Country.AustriaHungary, path: 'gysev/roee' },
    { short: 'ZBH', uicCode: 89, enumCountry: Country.BosniaAndHerzegovinaRailway, path: 'zbh' },
    { short: 'AF', uicCode: 68, enumCountry: Country.Afghanistan, path: 'af' },
    { short: 'AL', uicCode: 41, enumCountry: Country.Albania, path: 'al' },
    { short: 'DZ', uicCode: 92, enumCountry: Country.Algeria, path: 'dz' },
    { short: 'AM', uicCode: 58, enumCountry: Country.Armenia, path: 'am' },
    { short: 'AU', uicCode: 4, enumCountry: Country.Australia, path: 'au' },
    { short: 'AT', uicCode: 81, enumCountry: Country.Austria, path: 'at' },
    { short: 'LI', enumCountry: Country.Liechtenstein, path: 'li' },
  ];

  public static readonly geolocationCountries: Country[] = [
    Country.Switzerland,
    Country.GermanyBus,
    Country.AustriaBus,
    Country.ItalyBus,
    Country.FranceBus,
  ];

  static fromCountry(country: Country | undefined): CountryName | undefined {
    return this.countryNames.find((countryName) => countryName.enumCountry === country);
  }

  static fromUicCode(uicCountryCode: number) {
    return this.countryNames.find((countryName) => countryName.uicCode === uicCountryCode)!;
  }

  static getCountryNameUicCodeFromCountry(country: Country): number {
    const countryName = Countries.fromCountry(country);
    if (!countryName) return -1;
    return countryName.uicCode!;
  }

  static filteredCountries(): Country[] {
    const countriesToFilterOut: Country[] = [
      Country.Canada,
      Country.Congo,
      Country.SouthAfrica,
      Country.Australia,
      Country.Liechtenstein,
      Country.Sudan,
      Country.Tschad,
      Country.Libyen,
      Country.Monaco,
      Country.Niger,
      Country.Nigeria,
      Country.Jemen,
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus,
    ];
    return Object.values(Country).filter((country) => !countriesToFilterOut.includes(country));
  }

  static readonly getCountryEnum = (country: Country) =>
    Countries.fromCountry(country)?.enumCountry;

  public static readonly compareFn = (n1: Country, n2: Country) =>
    Countries.getCountryNameUicCodeFromCountry(n1) - Countries.getCountryNameUicCodeFromCountry(n2);
}
