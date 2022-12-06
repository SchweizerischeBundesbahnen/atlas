import { Page } from '../core/model/page';

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

  public static LIDI_WORKFLOW: Page = {
    title: 'PAGES.LIDI_WORKFLOW.TITLE',
    titleMenu: 'PAGES.LIDI_WORKFLOW.TITLE_MENU',
    headerTitle: 'PAGES.LIDI_WORKFLOW.TITLE_MENU',
    path: 'workflows',
    pathText: 'PAGES.LIDI_WORKFLOW.PATH_TEXT',
    description: 'PAGES.LIDI_WORKFLOW.DESCRIPTION',
  };

  public static LIDI_WORKFLOW_LINES: Page = {
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

  public static USER_ADMINISTRATION: Page = {
    title: 'PAGES.USER_ADMIN.TITLE',
    titleMenu: 'PAGES.USER_ADMIN.TITLE_HEADER',
    headerTitle: 'PAGES.USER_ADMIN.TITLE_HEADER',
    path: 'user-administration',
    pathText: 'PAGES.USER_ADMIN.TITLE_HEADER',
    description: 'PAGES.USER_ADMIN.DESCRIPTION',
  };

  public static BUSINESS_ORGANISATIONS: Page = {
    title: 'PAGES.DETAILS',
    path: 'business-organisations',
    pathText: '',
    description: '',
  };

  public static TRANSPORT_COMPANIES: Page = {
    title: 'PAGES.DETAILS',
    path: 'transport-companies',
    pathText: '',
    description: '',
  };

  public static COMPANIES: Page = {
    title: 'PAGES.DETAILS',
    path: 'companies',
    pathText: '',
    description: '',
  };

  public static pages: Page[] = [
    Pages.HOME,
    Pages.TTFN,
    Pages.LIDI,
    Pages.LIDI_WORKFLOW,
    Pages.BODI,
  ];
  public static adminPages: Page[] = [Pages.USER_ADMINISTRATION];
  public static enabledPages: Page[] = Pages.pages;
}
