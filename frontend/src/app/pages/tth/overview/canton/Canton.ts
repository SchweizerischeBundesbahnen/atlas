import { Page } from '../../../../core/model/page';
import { SwissCanton } from '../../../../api';

export interface Canton {
  short: string;
  long?: SwissCanton;
  page?: Page;
}
