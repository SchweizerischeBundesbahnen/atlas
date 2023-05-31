import { TthTableService } from './tth-table.service';
import { Pages } from '../pages';

describe('TthTableService', () => {
  let service: TthTableService;

  beforeEach(() => {
    service = new TthTableService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should enableFilters', () => {
    service.overviewDetailFilterConfigInternal.chipSearch.disabled = true;
    service.overviewDetailFilterConfigInternal.multiSelectStatementStatus.disabled = true;
    service.overviewDetailFilterConfigInternal.searchSelectTU.disabled = true;
    service.overviewDetailFilterConfigInternal.searchSelectTTFN.disabled = true;

    service.enableFilters();

    expect(service.overviewDetailFilterConfigInternal.chipSearch.disabled).toBeFalse();
    expect(
      service.overviewDetailFilterConfigInternal.multiSelectStatementStatus.disabled
    ).toBeFalse();
    expect(service.overviewDetailFilterConfigInternal.searchSelectTU.disabled).toBeFalse();
    expect(service.overviewDetailFilterConfigInternal.searchSelectTTFN.disabled).toBeFalse();
  });

  it('should disableFilters', () => {
    service.overviewDetailFilterConfigInternal.chipSearch.disabled = false;
    service.overviewDetailFilterConfigInternal.multiSelectStatementStatus.disabled = false;
    service.overviewDetailFilterConfigInternal.searchSelectTU.disabled = false;
    service.overviewDetailFilterConfigInternal.searchSelectTTFN.disabled = false;

    service.disableFilters();

    expect(service.overviewDetailFilterConfigInternal.chipSearch.disabled).toBeTrue();
    expect(
      service.overviewDetailFilterConfigInternal.multiSelectStatementStatus.disabled
    ).toBeTrue();
    expect(service.overviewDetailFilterConfigInternal.searchSelectTU.disabled).toBeTrue();
    expect(service.overviewDetailFilterConfigInternal.searchSelectTTFN.disabled).toBeTrue();
  });

  it('should reset tableSettings on set activeTabPage', () => {
    service.pageSize = 50;
    service.pageIndex = 50;
    service.sortActive = 'test';
    service.sortDirection = 'desc';

    spyOn<any>(service, 'createTableFilterConfigInternal');
    spyOn<any>(service, 'getTableFilterConfig');

    service.activeTabPage = Pages.TTH_ACTIVE;

    expect(service.pageSize).toEqual(10);
    expect(service.pageIndex).toEqual(0);
    expect(service.sortActive).toEqual('');
    expect(service.sortDirection).toEqual('asc');

    expect(service['createTableFilterConfigInternal']).toHaveBeenCalledOnceWith();
    expect(service['getTableFilterConfig']).toHaveBeenCalledOnceWith();
    expect(service['_activeTabPage']).toEqual(Pages.TTH_ACTIVE);
  });
});
