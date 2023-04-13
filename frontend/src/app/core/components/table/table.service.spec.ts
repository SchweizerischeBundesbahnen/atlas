import { TableService } from './table.service';

describe('TableService', () => {
  let service: TableService;

  beforeEach(() => {
    service = new TableService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return sortString', () => {
    service.sortActive = 'test';
    service.sortDirection = 'desc';
    expect(service.sortString).toEqual('test,desc');
  });

  it('should return undefined (sortString)', () => {
    service.sortActive = '';
    service.sortDirection = 'asc';
    expect(service.sortString).toBeUndefined();

    service.sortActive = 'test';
    service.sortDirection = '';
    expect(service.sortString).toBeUndefined();

    service.sortActive = '';
    service.sortDirection = '';
    expect(service.sortString).toBeUndefined();
  });
});
