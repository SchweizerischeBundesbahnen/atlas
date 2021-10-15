import { Page } from '../core/model/page';

export class Pages {
  public static HOME: Page = {
    title: 'PAGES.HOME',
    path: '',
    icon: 'bi-house-fill',
    description: '',
  };

  public static TTFN: Page = {
    title: 'PAGES.TTFN.TITLE',
    path: 'timetable-field-number',
    icon: 'bi-book-half',
    description: 'PAGES.TTFN.DESCRIPTION',
  };

  public static TTFN_DETAIL: Page = {
    title: 'PAGES.DETAILS',
    path: ':id',
    description: '',
  };

  public static LIDI: Page = {
    title: 'PAGES.LIDI.TITLE',
    path: 'line-directory',
    icon: 'bi-signpost-split',
    description: 'PAGES.LIDI.DESCRIPTION',
  };

  public static LINES: Page = {
    title: 'PAGES.DETAILS',
    path: 'lines',
    description: '',
  };

  public static SUBLINES: Page = {
    title: 'PAGES.DETAILS',
    path: 'sublines',
    description: '',
  };

  public static pages: Page[] = [Pages.HOME, Pages.TTFN, Pages.LIDI];
}
