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
    service.filterConfigInternal.chipSearch.disabled = true;
    service.filterConfigInternal.multiSelectStatementStatus.disabled = true;
    service.filterConfigInternal.searchSelectTU.disabled = true;
    service.filterConfigInternal.searchSelectTTFN.disabled = true;

    service.enableFilters();

    expect(service.filterConfigInternal.chipSearch.disabled).toBeFalse();
    expect(service.filterConfigInternal.multiSelectStatementStatus.disabled).toBeFalse();
    expect(service.filterConfigInternal.searchSelectTU.disabled).toBeFalse();
    expect(service.filterConfigInternal.searchSelectTTFN.disabled).toBeFalse();
  });

  it('should disableFilters', () => {
    service.filterConfigInternal.chipSearch.disabled = false;
    service.filterConfigInternal.multiSelectStatementStatus.disabled = false;
    service.filterConfigInternal.searchSelectTU.disabled = false;
    service.filterConfigInternal.searchSelectTTFN.disabled = false;

    service.disableFilters();

    expect(service.filterConfigInternal.chipSearch.disabled).toBeTrue();
    expect(service.filterConfigInternal.multiSelectStatementStatus.disabled).toBeTrue();
    expect(service.filterConfigInternal.searchSelectTU.disabled).toBeTrue();
    expect(service.filterConfigInternal.searchSelectTTFN.disabled).toBeTrue();
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
