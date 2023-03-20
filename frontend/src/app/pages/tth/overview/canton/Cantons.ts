import { Canton } from './Canton';
import { SwissCanton } from '../../../../api';

export class Cantons {
  public static cantons: Canton[] = [
    { short: 'AG', long: SwissCanton.Aargau },
    { short: 'AI', long: SwissCanton.AppenzellInnerrhoden },
    { short: 'AR', long: SwissCanton.AppenzellAusserrhoden },
    { short: 'BE', long: SwissCanton.Bern },
    { short: 'BL', long: SwissCanton.BaselCountry },
    { short: 'BS', long: SwissCanton.BaselCity },
    { short: 'FR', long: SwissCanton.Fribourg },
    { short: 'GE', long: SwissCanton.Geneve },
    { short: 'GL', long: SwissCanton.Glarus },
    { short: 'GR', long: SwissCanton.Graubunden },
    { short: 'JU', long: SwissCanton.Jura },
    { short: 'LU', long: SwissCanton.Lucerne },
    { short: 'NE', long: SwissCanton.Neuchatel },
    { short: 'NW', long: SwissCanton.Nidwalden },
    { short: 'OW', long: SwissCanton.Obwalden },
    { short: 'SG', long: SwissCanton.StGallen },
    { short: 'SH', long: SwissCanton.Schaffhausen },
    { short: 'SO', long: SwissCanton.Solothurn },
    { short: 'SZ', long: SwissCanton.Schwyz },
    { short: 'TG', long: SwissCanton.Thurgau },
    { short: 'TI', long: SwissCanton.Ticino },
    { short: 'UR', long: SwissCanton.Uri },
    { short: 'VD', long: SwissCanton.Vaud },
    { short: 'VS', long: SwissCanton.Valais },
    { short: 'ZG', long: SwissCanton.Zug },
    { short: 'ZH', long: SwissCanton.Zurich },
  ];

  public static swiss: Canton = {
    short: 'CH',
  };

  public static fromSwissCanton(swissCanton: SwissCanton): Canton | undefined {
    return this.cantons.find((canton) => canton.long === swissCanton);
  }
}
