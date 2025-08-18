import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  RouterStateSnapshot,
} from '@angular/router';

import { toiletResolver } from './toilet.resolver';
import {
  ReadToiletVersion,
  StandardAttributeType,
  ToiletVersion,
} from '../../../../../../api';
import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { ToiletService } from '../../../../../../api/service/prm/toilet/toilet.service';

const toiletVersions: ReadToiletVersion[] = [
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: '***REMOVED***',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: '***REMOVED***',
    id: 1000,
    sloid: 'ch:1:sloid:12345:1',
    validFrom: new Date('2000-01-01'),
    validTo: new Date('2000-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    designation: 'designation',
    additionalInformation: 'additional',
    wheelchairToilet: StandardAttributeType.Yes,
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
  },
];

describe('toiletResolver', () => {
  const toiletServiceSpy = jasmine.createSpyObj('toiletService', [
    'getToiletVersions',
  ]);
  toiletServiceSpy.getToiletVersions.and.returnValue(of(toiletVersions));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        {
          provide: ToiletService,
          useValue: toiletServiceSpy,
        },
      ],
    });
  });

  it('should get toiletVersion from prm-directory', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:12345:1' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      toiletResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<ToiletVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:12345:1');
    });
  });
});
