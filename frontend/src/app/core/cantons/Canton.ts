import { SwissCanton } from '../../api';

export interface Canton {
  short: string;
  enumCanton?: SwissCanton;
  path: string;
}
