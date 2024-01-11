import { TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { PersonWithReducedMobilityService, ReadPlatformVersion } from '../../../../api';
import { platformResolver } from './platform.resolver';
import { PrmPanelResolver } from '../../prm-panel/resolvers/prm-panel-resolver.service';

describe('PrmPlatformResolver', () => {
  const personWithReducedMobilityServiceSpy = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getPlatformVersions'],
  );
  personWithReducedMobilityServiceSpy.getPlatformVersions.and.returnValue(of([]));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        PrmPanelResolver,
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityServiceSpy,
        },
      ],
    });
  });

  it('should get platform from prm-directory', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:89008' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      platformResolver(mockRoute, {} as RouterStateSnapshot),
    ) as Observable<ReadPlatformVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008');
    });
  });
});
