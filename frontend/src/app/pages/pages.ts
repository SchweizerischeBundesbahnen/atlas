import { Page } from '../core/model/page';
import { environment } from '../../environments/environment';

export class Pages {
  public static HOME: Page = {
    title: 'PAGES.HOME',
    titleMenu: 'PAGES.HOME_MENU',
    headerTitle: 'PAGES.HOME',
    path: '',
    pathText: '',
    description: '',
  };

  public static TTFN: Page = {
    title: 'PAGES.TTFN.TITLE',
    titleMenu: 'PAGES.TTFN.TITLE_MENU',
    headerTitle: 'PAGES.TTFN.TITLE_MENU',
    path: 'timetable-field-number',
    pathText: 'PAGES.TTFN.PATH_TEXT',
    description: 'PAGES.TTFN.DESCRIPTION',
  };

  public static TTFN_DETAIL: Page = {
    title: 'PAGES.DETAILS',
    path: ':id',
    pathText: '',
    description: '',
  };

  public static LIDI: Page = {
    title: 'PAGES.LIDI.TITLE',
    titleMenu: 'PAGES.LIDI.TITLE_MENU',
    headerTitle: 'PAGES.LIDI.TITLE_MENU',
    path: 'line-directory',
    pathText: 'PAGES.LIDI.PATH_TEXT',
    description: 'PAGES.LIDI.DESCRIPTION',
  };

  public static LINES: Page = {
    title: 'PAGES.DETAILS',
    path: 'lines',
    pathText: '',
    description: '',
  };

  public static SUBLINES: Page = {
    title: 'PAGES.DETAILS',
    path: 'sublines',
    pathText: '',
    description: '',
  };

  public static BODI: Page = {
    title: 'PAGES.BODI.TITLE',
    titleMenu: 'PAGES.BODI.TITLE_MENU',
    headerTitle: 'PAGES.BODI.TITLE_HEADER',
    path: 'business-organisation-directory',
    pathText: 'PAGES.BODI.PATH_TEXT',
    description: 'PAGES.BODI.DESCRIPTION',
  };

  public static BUSINESS_ORGANISATIONS: Page = {
    title: 'PAGES.DETAILS',
    path: 'business-organisation',
    pathText: '',
    description: '',
  };

  public static TRANSPORT_COMPANIES: Page = {
    title: 'PAGES.DETAILS',
    path: 'transport-companies',
    pathText: '',
    description: '',
  };

  private static pages: Page[] = [Pages.HOME, Pages.TTFN, Pages.LIDI, Pages.BODI];

  public static enabledPages(): Page[] {
    let enabledPages = this.pages;
    if (!environment.pageBodiEnabled) {
      enabledPages = enabledPages.filter((page) => page !== Pages.BODI);
    }
    return enabledPages;
  }
}
