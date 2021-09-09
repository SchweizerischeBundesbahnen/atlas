import { Page } from '../core/model/page';

export class Pages {
  public static HOME: Page = {
    title: 'PAGES.HOME',
    path: '',
    icon: 'bi-house-fill',
  };

  public static AUTH_INSIGHT: Page = {
    title: 'PAGES.AUTH_INSIGHT',
    path: 'auth-insights',
    icon: 'bi-key-fill',
  };

  public static TTFN_DETAILS: Page = {
    title: 'PAGES.DETAILS',
    path: ':id',
  };

  public static pages: Page[] = [Pages.HOME, Pages.AUTH_INSIGHT];
}
