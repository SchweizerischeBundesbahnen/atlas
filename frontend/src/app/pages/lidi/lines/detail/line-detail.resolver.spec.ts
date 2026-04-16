import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { LineType, LineVersionV2, Status } from '../../../../api';
import { LineDetailResolver } from './line-detail.resolver';
import { AppTestingModule } from '../../../../app.testing.module';
import { LineService } from '../../../../api/service/lidi/line.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const version: LineVersionV2 = {
  lineConcessionType: 'CANTONALLY_APPROVED_LINE',
  offerCategory: 'BAT',
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: Status.Validated,
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  swissLineNumber: 'L1',
  lineType: LineType.Orderly,
};

describe('LineDetailResolver', () => {
  let resolver: LineDetailResolver;
  let lineService: Mocked<Pick<LineService, 'getLineVersionsV2'>>;

  beforeEach(() => {
    lineService = {
      getLineVersionsV2: vi.fn(),
    };
    lineService.getLineVersionsV2.mockReturnValue(of([version]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [LineDetailResolver, { provide: LineService, useValue: lineService }],
    });

    resolver = TestBed.inject(LineDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    const versions = await firstValueFrom(resolvedVersion);
    expect(versions.length).toBe(1);
    expect(versions[0].id).toBe(1234);
    expect(versions[0].status).toBe(Status.Validated);
    expect(versions[0].slnid).toBe('slnid');
  });
});
