import {TestBed} from '@angular/core/testing';
import {ActivatedRouteSnapshot, convertToParamMap} from '@angular/router';
import {of} from 'rxjs';
import {ReadSublineVersionV2, Status, SublinesService, SublineType} from '../../../../api';
import {SublineDetailResolver} from './subline-detail.resolver';
import {AppTestingModule} from '../../../../app.testing.module';

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
  mainLineNumber:'mainLineNumber',
  mainSwissLineNumber:'mainSwissLineNumber',
};

describe('SublineDetailResolver', () => {
  const sublinesServiceSpy = jasmine.createSpyObj('sublinesService', ['getSublineVersionV2']);
  sublinesServiceSpy.getSublineVersionV2.and.returnValue(of([version]));

  let resolver: SublineDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        SublineDetailResolver,
        { provide: SublinesService, useValue: sublinesServiceSpy },
      ],
    });
    resolver = TestBed.inject(SublineDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(1234);
      expect(versions[0].status).toBe(Status.Validated);
      expect(versions[0].slnid).toBe('slnid');
    });
  });
});
