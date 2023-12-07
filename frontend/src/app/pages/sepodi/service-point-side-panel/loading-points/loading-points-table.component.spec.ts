import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsTableComponent } from './loading-points-table.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { LoadingPointsService } from '../../../../api';
import { LOADING_POINT } from '../../../../../test/data/loading-point';

describe('LoadingPointsTableComponent', () => {
  let component: LoadingPointsTableComponent;
  let fixture: ComponentFixture<LoadingPointsTableComponent>;

  const loadingPointService = jasmine.createSpyObj('LoadingPointsService', [
    'getLoadingPointOverview',
  ]);
  loadingPointService.getLoadingPointOverview.and.returnValue(of(LOADING_POINT));
  const route = { parent: { snapshot: { params: { id: 8504414 } } } };
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingPointsTableComponent, MockTableComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: LoadingPointsService, useValue: loadingPointService },
      ],
    }).compileComponents();

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

    expect(loadingPointService.getLoadingPointOverview).toHaveBeenCalledOnceWith(8504414, 0, 10, [
      'designation,asc',
    ]);
  });

  it('should go to new', () => {
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.newLoadingPoint();
    expect(router.navigate).toHaveBeenCalledWith([
      'service-point-directory',
      'loading-points',
      8504414,
      'add',
    ]);
  });

  it('should go to edit', () => {
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.editVersion(LOADING_POINT[0]);
    expect(router.navigate).toHaveBeenCalledWith([
      'service-point-directory',
      'loading-points',
      8504414,
      1231,
    ]);
  });

  it('should close side panel', () => {
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.closeSidePanel();
    expect(router.navigate).toHaveBeenCalledWith(['service-point-directory']);
  });
});
