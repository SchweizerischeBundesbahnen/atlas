import { Pages } from '../../pages/pages';

export interface ServicePointSearchType {
  navigationPath: string;
}

export class ServicePointSearch {
  public static PRM: ServicePointSearchType = { navigationPath: Pages.STOP_POINTS.path };
  public static SePoDi: ServicePointSearchType = { navigationPath: Pages.SERVICE_POINTS.path };
}
