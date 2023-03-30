import { TableService } from './table.service';

describe('TableService', () => {
  let service: TableService;

  beforeEach(() => {
    service = new TableService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
