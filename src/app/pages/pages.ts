import { Page } from '../core/model/page';

export class Pages {
  public static HOME: Page = {
    title: 'PAGES.HOME',
    path: '',
    icon: 'bi-house-fill',
  };

  public static TTFN: Page = {
    title: 'PAGES.TTFN',
    path: 'timetable-field-number',
    icon: 'bi-book-half',
  };

  public static LIDI: Page = {
    title: 'PAGES.LIDI',
    path: 'line-directory',
    icon: 'bi-signpost-split',
  };

  public static pages: Page[] = [Pages.HOME, Pages.TTFN, Pages.LIDI];
}
