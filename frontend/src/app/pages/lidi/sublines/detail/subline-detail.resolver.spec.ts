import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { ReadSublineVersionV2, Status, SublineType } from '../../../../api';
import { SublineDetailResolver } from './subline-detail.resolver';
import { AppTestingModule } from '../../../../app.testing.module';
import { SublineService } from '../../../../api/service/lidi/subline.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const version: ReadSublineVersionV2 = {
  id: 1234,
  slnid: 'slnid',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  swissSublineNumber: 'L1:2',
  sublineType: SublineType.Technical,
  mainlineSlnid: 'ch:1:slnid:1000',
  mainLineNumber: 'mainLineNumber',
  mainSwissLineNumber: 'mainSwissLineNumber',
};

describe('SublineDetailResolver', () => {
  let resolver: SublineDetailResolver;
  let sublineService: Mocked<Pick<SublineService, 'getSublineVersionV2'>>;

  beforeEach(() => {
    sublineService = {
      getSublineVersionV2: vi.fn(),
    };
    sublineService.getSublineVersionV2.mockReturnValue(of([version]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [SublineDetailResolver, { provide: SublineService, useValue: sublineService }],
    });

    resolver = TestBed.inject(SublineDetailResolver);
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
