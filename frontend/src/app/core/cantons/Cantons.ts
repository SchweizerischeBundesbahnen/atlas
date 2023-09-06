import { Canton } from './Canton';
import { SwissCanton } from '../../api';

export class Cantons {
  public static cantons: Canton[] = [
    { short: 'AG', enumCanton: SwissCanton.Aargau, path: 'ag' },
    { short: 'AI', enumCanton: SwissCanton.AppenzellInnerrhoden, path: 'ai' },
    { short: 'AR', enumCanton: SwissCanton.AppenzellAusserrhoden, path: 'ar' },
    { short: 'BE', enumCanton: SwissCanton.Bern, path: 'be' },
    { short: 'BL', enumCanton: SwissCanton.BaselCountry, path: 'bl' },
    { short: 'BS', enumCanton: SwissCanton.BaselCity, path: 'bs' },
    { short: 'FR', enumCanton: SwissCanton.Fribourg, path: 'fr' },
    { short: 'GE', enumCanton: SwissCanton.Geneve, path: 'ge' },
    { short: 'GL', enumCanton: SwissCanton.Glarus, path: 'gl' },
    { short: 'GR', enumCanton: SwissCanton.Graubunden, path: 'gr' },
    { short: 'JU', enumCanton: SwissCanton.Jura, path: 'ju' },
    { short: 'LU', enumCanton: SwissCanton.Lucerne, path: 'lu' },
    { short: 'NE', enumCanton: SwissCanton.Neuchatel, path: 'ne' },
    { short: 'NW', enumCanton: SwissCanton.Nidwalden, path: 'nw' },
    { short: 'OW', enumCanton: SwissCanton.Obwalden, path: 'ow' },
    { short: 'SG', enumCanton: SwissCanton.StGallen, path: 'sg' },
    { short: 'SH', enumCanton: SwissCanton.Schaffhausen, path: 'sh' },
    { short: 'SO', enumCanton: SwissCanton.Solothurn, path: 'so' },
    { short: 'SZ', enumCanton: SwissCanton.Schwyz, path: 'sz' },
    { short: 'TG', enumCanton: SwissCanton.Thurgau, path: 'tg' },
    { short: 'TI', enumCanton: SwissCanton.Ticino, path: 'ti' },
    { short: 'UR', enumCanton: SwissCanton.Uri, path: 'ur' },
    { short: 'VD', enumCanton: SwissCanton.Vaud, path: 'vd' },
    { short: 'VS', enumCanton: SwissCanton.Valais, path: 'vs' },
    { short: 'ZG', enumCanton: SwissCanton.Zug, path: 'zg' },
    { short: 'ZH', enumCanton: SwissCanton.Zurich, path: 'zh' },
  ];

  public static swiss: Canton = {
    short: 'CH',
    path: 'ch',
    enumCanton: 'SWISS' as SwissCanton,
  };

  public static cantonsWithSwiss: Canton[] = [Cantons.swiss].concat(Cantons.cantons);

  public static getSwissCantonEnum(canton: string | null): SwissCanton | undefined {
    if (!canton) {
      return undefined;
    }
    const foundCanton = Cantons.cantonsWithSwiss.find((c) => {
      return c.short.toLowerCase() === canton?.toLowerCase();
    });
    if (!foundCanton) {
      return undefined;
    }
    return foundCanton.enumCanton;
  }

  public static fromSwissCanton(swissCanton: SwissCanton | undefined): Canton | undefined {
    return this.cantons.find((canton) => canton.enumCanton === swissCanton);
  }
}
