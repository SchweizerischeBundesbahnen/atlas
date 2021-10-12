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

  public static LIDI: Page = {
    title: 'PAGES.LIDI.TITLE',
    path: 'line-directory',
    icon: 'bi-signpost-split',
    description: 'PAGES.LIDI.DESCRIPTION',
  };

  public static pages: Page[] = [Pages.HOME, Pages.TTFN, Pages.LIDI];
}
