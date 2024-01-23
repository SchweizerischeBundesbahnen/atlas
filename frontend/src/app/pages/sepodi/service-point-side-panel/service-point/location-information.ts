import { SwissCanton } from '../../../../api';

export interface LocationInformation {
  isoCountryCode?: string;
  canton?: SwissCanton;
  municipalityName?: string;
  localityName?: string;
  height?: number;
}
