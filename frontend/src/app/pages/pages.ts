import { Page } from '../core/model/page';
import { environment } from '../../environments/environment';

export class Pages {
  public static HOME: Page = {
    title: 'PAGES.HOME',
    titleMenu: 'PAGES.HOME_MENU',
    headerTitle: 'PAGES.HOME',
    path: '',
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
  };

  public static SUBLINES: Page = {
    title: 'PAGES.DETAILS',
    path: 'sublines',
  };

  public static WORKFLOWS: Page = {
    title: 'PAGES.LIDI_WORKFLOW.TITLE',
    path: 'workflows',
  };

  public static BODI: Page = {
    title: 'PAGES.BODI.TITLE',
    titleMenu: 'PAGES.BODI.TITLE_MENU',
    headerTitle: 'PAGES.BODI.TITLE_HEADER',
    path: 'business-organisation-directory',
    pathText: 'PAGES.BODI.PATH_TEXT',
    description: 'PAGES.BODI.DESCRIPTION',
  };

  public static USER_ADMINISTRATION: Page = {
    title: 'PAGES.USER_ADMIN.TITLE',
    titleMenu: 'PAGES.USER_ADMIN.TITLE_HEADER',
    headerTitle: 'PAGES.USER_ADMIN.TITLE_HEADER',
    path: 'user-administration',
    pathText: 'PAGES.USER_ADMIN.TITLE_HEADER',
    description: 'PAGES.USER_ADMIN.DESCRIPTION',
  };

  public static USERS: Page = {
    title: 'PAGES.DETAILS',
    path: 'users',
  };

  public static CLIENTS: Page = {
    title: 'PAGES.DETAILS',
    path: 'clients',
  };

  public static TTH: Page = {
    title: 'PAGES.TTH.TITLE',
    titleMenu: 'PAGES.TTH.TITLE_MENU',
    headerTitle: 'PAGES.TTH.TITLE_MENU',
    path: 'timetable-hearing',
    pathText: 'PAGES.TTH.PATH_TEXT',
    description: 'PAGES.TTH.DESCRIPTION',
  };

  public static TTH_ACTIVE: Page = {
    title: 'PAGES.DETAILS',
    path: 'active',
  };

  public static TTH_PLANNED: Page = {
    title: 'PAGES.DETAILS',
    path: 'planned',
  };

  public static TTH_ARCHIVED: Page = {
    title: 'PAGES.DETAILS',
    path: 'archived',
  };

  public static TTH_OVERVIEW_DETAIL: Page = {
    title: 'PAGES.OVERVIEW_DETAILS',
    path: ':canton',
  };

  public static TTH_STATEMENT_DETAILS: Page = {
    title: 'PAGES.DETAILS',
    path: ':id',
  };

  public static BUSINESS_ORGANISATIONS: Page = {
    title: 'PAGES.DETAILS',
    path: 'business-organisations',
  };

  public static TRANSPORT_COMPANIES: Page = {
    title: 'PAGES.DETAILS',
    path: 'transport-companies',
  };

  public static COMPANIES: Page = {
    title: 'PAGES.DETAILS',
    path: 'companies',
  };

  public static SEPODI: Page = {
    title: 'PAGES.SERVICE_POINTS.TITLE',
    titleMenu: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    headerTitle: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    path: 'service-point-directory',
    pathText: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    description: 'PAGES.SERVICE_POINTS.DESCRIPTION',
  };

  public static SERVICE_POINTS: Page = {
    title: 'PAGES.SERVICE_POINTS.TITLE',
    titleMenu: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    headerTitle: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    path: 'service-points',
    pathText: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    description: 'PAGES.SERVICE_POINTS.DESCRIPTION',
  };

  public static TRAFFIC_POINT_ELEMENTS_PLATFORM: Page = {
    title: 'PAGES.SERVICE_POINTS.TITLE',
    titleMenu: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    headerTitle: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    path: 'traffic-point-elements',
    pathText: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    description: 'PAGES.SERVICE_POINTS.DESCRIPTION',
  };

  public static TRAFFIC_POINT_ELEMENTS_AREA: Page = {
    title: 'PAGES.SERVICE_POINTS.TITLE',
    titleMenu: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    headerTitle: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    path: 'areas',
    pathText: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    description: 'PAGES.SERVICE_POINTS.DESCRIPTION',
  };

  public static LOADING_POINTS: Page = {
    title: 'PAGES.SERVICE_POINTS.TITLE',
    titleMenu: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    headerTitle: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    path: 'loading-points',
    pathText: 'PAGES.SERVICE_POINTS.TITLE_HEADER',
    description: 'PAGES.SERVICE_POINTS.DESCRIPTION',
  };

  public static PRM: Page = {
    title: 'PAGES.PRM.TITLE',
    titleMenu: 'PAGES.PRM.TITLE_HEADER',
    headerTitle: 'PAGES.PRM.TITLE_HEADER',
    path: 'prm-directory',
    pathText: 'PAGES.PRM.TITLE_HEADER',
    description: 'PAGES.PRM.DESCRIPTION',
  };

  public static STOP_POINTS: Page = {
    title: 'PRM.STOP_POINTS.TITLE',
    titleMenu: 'PAGES.STOP_POINTS.TITLE_HEADER',
    headerTitle: 'PAGES.STOP_POINTS.TITLE_HEADER',
    path: 'stop-points',
    pathText: 'PAGES.STOP_POINTS.TITLE_HEADER',
    description: 'PAGES.STOP_POINTS.DESCRIPTION',
  };

  public static PRM_STOP_POINT_TAB: Page = {
    title: 'PRM.TABS.STOP_POINT',
    path: 'stop-point',
  };

  public static pages: Page[] = [
    Pages.HOME,
    Pages.TTFN,
    Pages.LIDI,
    Pages.BODI,
    Pages.SEPODI,
    Pages.PRM,
  ];
  public static adminPages: Page[] = [Pages.USER_ADMINISTRATION];
  public static viewablePages: Page[] = this.pages;

  public static get enabledPages(): Page[] {
    let enabledPages = this.viewablePages;
    if (!environment.pageSepodiEnabled) {
      enabledPages = enabledPages.filter((page) => page !== Pages.SEPODI && page !== Pages.PRM);
    }
    return enabledPages;
  }
}
