import { TestBed } from '@angular/core/testing';

import { TabService } from './tab.service';
import { Pages } from './pages';

describe('TabServiceService', () => {
  let service: TabService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TabService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return index 2', () => {
    //given
    const TABS = [
      {
        link: Pages.LINES.path,
        title: 'LIDI.LINE.LINES',
      },
      {
        link: Pages.SUBLINES.path,
        title: 'LIDI.SUBLINE.SUBLINES',
      },
      {
        link: Pages.WORKFLOWS.path,
        title: 'LIDI.LINE_VERSION_SNAPSHOT.TAB_HEADER',
      },
    ];
    const url = 'http://localhost:4200/line-directory/workflows';
    //when
    const currentTabIndex = service.getCurrentTabIndex(url, TABS);

    //then
    expect(currentTabIndex).toBe(2);
  });

  it('should return index -1', () => {
    //given
    const TABS = [
      {
        link: Pages.LINES.path,
        title: 'LIDI.LINE.LINES',
      },
      {
        link: Pages.SUBLINES.path,
        title: 'LIDI.SUBLINE.SUBLINES',
      },
      {
        link: Pages.WORKFLOWS.path,
        title: 'LIDI.LINE_VERSION_SNAPSHOT.TAB_HEADER',
      },
    ];
    const url = 'http://localhost:4200/line-directory/workflows';
    //when
    const currentTabIndex = service.getCurrentTabIndex(url, TABS);

    //then
    expect(currentTabIndex).toBe(-1);
  });
});
