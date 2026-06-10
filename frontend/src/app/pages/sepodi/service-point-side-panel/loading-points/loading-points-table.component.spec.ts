import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

import { LoadingPointsTableComponent } from './loading-points-table.component';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { LOADING_POINT } from '../../../../../test/data/loading-point';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { LoadingPointInternalService } from '../../../../api/service/sepodi/loading-point-internal.service';

describe('LoadingPointsTableComponent', () => {
  let component: LoadingPointsTableComponent;
  let fixture: ComponentFixture<LoadingPointsTableComponent>;

  let loadingPointInternalServiceSpy: Mocked<Pick<LoadingPointInternalService, 'getLoadingPointOverview'>>;

  const route = {
    parent: { snapshot: { params: { servicePointNumber: 8504414 } } },
  };
  let router: Router;

  beforeEach(async () => {
    loadingPointInternalServiceSpy = {
      getLoadingPointOverview: vi.fn(),
    };
    loadingPointInternalServiceSpy.getLoadingPointOverview.mockReturnValue(of({ objects: LOADING_POINT }));

    await TestBed.configureTestingModule({
      imports: [LoadingPointsTableComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: LoadingPointInternalService,
          useValue: loadingPointInternalServiceSpy,
        },
      ],
    })
      .overrideComponent(LoadingPointsTableComponent, {
        remove: { imports: [AtlasButtonComponent, TableComponent] },
        add: { imports: [MockAtlasButtonComponent, MockTableComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(LoadingPointsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display loading points', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(loadingPointInternalServiceSpy.getLoadingPointOverview).toHaveBeenCalledWith(8504414, 0, 10, [
      'designation,asc',
    ]);
  });

  it('should go to new', () => {
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.newLoadingPoint();
    expect(router.navigate).toHaveBeenCalledWith(['service-point-directory', 'loading-points', 8504414, 'add']);
  });

  it('should go to edit', () => {
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.editVersion(LOADING_POINT[0]);
    expect(router.navigate).toHaveBeenCalledWith(['service-point-directory', 'loading-points', 8504414, 1231]);
  });

  it('should close side panel', () => {
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.closeSidePanel();
    expect(router.navigate).toHaveBeenCalledWith(['service-point-directory']);
  });
});
