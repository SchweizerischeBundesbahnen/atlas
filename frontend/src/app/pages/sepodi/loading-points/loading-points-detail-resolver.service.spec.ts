import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { LoadingPointsService } from '../../../api';
import { TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../app.testing.module';
import { LoadingPointsDetailResolver } from './loading-points-detail-resolver.service';
import { LOADING_POINT } from '../../../../test/data/loading-point';

describe('LoadingPointsDetailResolver', () => {
  const loadingPointsService = jasmine.createSpyObj('loadingPointsService', ['getLoadingPoint']);
  loadingPointsService.getLoadingPoint.and.returnValue(of(LOADING_POINT));

  let resolver: LoadingPointsDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        LoadingPointsDetailResolver,
        { provide: LoadingPointsService, useValue: loadingPointsService },
      ],
    });
    resolver = TestBed.inject(LoadingPointsDetailResolver);
  });

  it('should get versions from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ servicePointNumber: '8504414', number: 1231 }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(2);
      expect(versions[0].number).toBe(1231);
      expect(versions[0].id).toBe(1255);
    });
  });
});
