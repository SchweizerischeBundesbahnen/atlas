import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { LinesService, LineVersion } from '../../../../api/lidi';
import { LineDetailResolver } from './line-detail.resolver';
import StatusEnum = LineVersion.StatusEnum;

const version: LineVersion = {
  id: 1234,
  slnid: 'slnid',
  shortName: 'name',
  description: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

describe('LineDetailResolver', () => {
  const linesServiceSpy = jasmine.createSpyObj('linesService', ['getLineVersion']);
  linesServiceSpy.getLineVersion.and.returnValue(of(version));

  let resolver: LineDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [LineDetailResolver, { provide: LinesService, useValue: linesServiceSpy }],
    });
    resolver = TestBed.inject(LineDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((version) => {
      expect(version.id).toBe(1234);
      expect(version.status).toBe(StatusEnum.Active);
      expect(version.slnid).toBe('slnid');
    });
  });
});
