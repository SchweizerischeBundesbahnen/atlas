import { TableSettingsService } from './table-settings.service';
import { TestBed } from '@angular/core/testing';
import { Event, NavigationEnd, NavigationStart } from '@angular/router';
import { Subject } from 'rxjs';
import { Pages } from '../../../pages/pages';
import { RouterTestingModule } from '@angular/router/testing';

const tableSettings = {
  page: 1,
  size: 10,
};

describe('TableSettingsService', () => {
  let service: TableSettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    service = TestBed.inject(TableSettingsService);

    service.storeTableSettings(Pages.TTFN.path, tableSettings);
  });

  it('should get tableSettings', () => {
    expect(service.getTableSettings(Pages.TTFN.path)).toEqual(tableSettings);
  });

  it('should get tableSettings when navigating from details back to overview', () => {
    const startUrl = '/' + Pages.TTFN.path + '/ch:1:ttfnid123';
    spyOnProperty(service.router, 'url').and.returnValue(startUrl);
    const routerEvents = service.router.events as Subject<Event>;
    routerEvents.next(new NavigationStart(0, startUrl));
    routerEvents.next(new NavigationEnd(0, '/' + Pages.TTFN.path, '/' + Pages.TTFN.path));
    expect(service.getTableSettings(Pages.TTFN.path)).toEqual(tableSettings);
  });

  it('should get tableSettings when navigating from add dialog back to overview', () => {
    const startUrl = '/' + Pages.TTFN.path + '/add';
    spyOnProperty(service.router, 'url').and.returnValue(startUrl);
    const routerEvents = service.router.events as Subject<Event>;
    routerEvents.next(new NavigationStart(0, startUrl));
    routerEvents.next(new NavigationEnd(0, '/' + Pages.TTFN.path, '/' + Pages.TTFN.path));
    expect(service.getTableSettings(Pages.TTFN.path)).toEqual(tableSettings);
  });

  it('should get tableSettings when navigating from detail to detail on save', () => {
    const startUrl = '/' + Pages.TTFN.path + '/ch:1:ttfnid123';
    spyOnProperty(service.router, 'url').and.returnValue(startUrl);
    const routerEvents = service.router.events as Subject<Event>;
    routerEvents.next(new NavigationStart(0, startUrl));
    routerEvents.next(new NavigationEnd(0, startUrl, startUrl));
    expect(service.getTableSettings(Pages.TTFN.path)).toEqual(tableSettings);
  });

  it('should get empty tableSettings when reloading overview', () => {
    const startUrl = '/' + Pages.TTFN.path;
    spyOnProperty(service.router, 'url').and.returnValue(startUrl);
    const routerEvents = service.router.events as Subject<Event>;
    routerEvents.next(new NavigationStart(0, startUrl));
    routerEvents.next(new NavigationEnd(0, '/' + Pages.TTFN.path, '/' + Pages.TTFN.path));
    expect(service.getTableSettings(Pages.TTFN.path)).toBeUndefined();
  });
});
