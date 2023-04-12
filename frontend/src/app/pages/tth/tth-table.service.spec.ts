import { TthTableService } from './tth-table.service';

describe('TthTableService', () => {
  let service: TthTableService;

  beforeEach(() => {
    service = new TthTableService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
