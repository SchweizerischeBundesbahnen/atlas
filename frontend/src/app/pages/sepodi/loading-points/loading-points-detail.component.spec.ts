import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import {
  ActivatedRouteMockType,
  translateServiceProvider,
} from '../../../app.testing.mocks';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import moment from 'moment/moment';
import { LoadingPointsDetailComponent } from './loading-points-detail.component';
import { LOADING_POINT } from '../../../../test/data/loading-point';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { ServicePointService } from '../../../api/service/sepodi/service-point.service';
import { LoadingPointService } from '../../../api/service/sepodi/loading-point.service';
import { DateModule } from '../../../core/module/date.module';

describe('LoadingPointsDetailComponent', () => {
  const authService: Partial<AuthService> = {};
  let component: LoadingPointsDetailComponent;
  let fixture: ComponentFixture<LoadingPointsDetailComponent>;
  let router: Router;

  let servicePointService: Mocked<
    Pick<ServicePointService, 'getServicePointVersions'>
  >;
  let loadingPointService: Mocked<
    Pick<LoadingPointService, 'createLoadingPoint' | 'updateLoadingPoint'>
  >;
  let dialogService: Mocked<
    Pick<DialogService, 'openDialogDataWithConfirmationResult'>
  >;

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    servicePointService = {
      getServicePointVersions: vi.fn(),
    };
    servicePointService.getServicePointVersions.mockReturnValue(
      of([BERN_WYLEREGG])
    );

    loadingPointService = {
      createLoadingPoint: vi.fn(),
      updateLoadingPoint: vi.fn(),
    };
    loadingPointService.createLoadingPoint.mockReturnValue(
      of(LOADING_POINT[0])
    );
    loadingPointService.updateLoadingPoint.mockReturnValue(of(LOADING_POINT));

    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(
      of(true)
    );

    Element.prototype.scrollIntoView = vi.fn();
    TestBed.configureTestingModule({
      imports: [DateModule.forRoot()],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: ServicePointService, useValue: servicePointService },
        { provide: LoadingPointService, useValue: loadingPointService },
        { provide: DialogService, useValue: dialogService },
        SplitServicePointNumberPipe,
        translateServiceProvider,
      ],
    });
  }

  describe('for existing Version', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({ loadingPoint: LOADING_POINT }),
        snapshot: { params: { servicePointNumber: 8504414 } },
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(LoadingPointsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should display validity', () => {
      expect(component.selectedVersion).toBeTruthy();

      expect(component.maxValidity.validFrom).toEqual(new Date('2023-11-01'));
      expect(component.maxValidity.validTo).toEqual(new Date('2099-11-07'));
    });

    it('should init selected servicepoint', () => {
      expect(component.servicePointName).toBeTruthy();
      expect(component.servicePoint).toBeTruthy();
      expect(component.servicePointBusinessOrganisations).toBeTruthy();

      expect(servicePointService.getServicePointVersions).toHaveBeenCalled();
    });

    it('should go back to servicepoint', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      component.backToServicePoint();

      expect(router.navigate).toHaveBeenCalledWith([
        'service-point-directory',
        'service-points',
        8504414,
        'loading-points',
      ]);
    });

    it('should toggle form correctly', () => {
      expect(component.form.enabled).toBe(false);

      component.toggleEdit();
      expect(component.form.enabled).toBe(true);

      component.toggleEdit();
      expect(component.form.enabled).toBe(false);
    });

    it('should update via service', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      component.toggleEdit();
      component.save();

      expect(loadingPointService.updateLoadingPoint).toHaveBeenCalled();
    });
  });

  describe('for new Version', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({ loadingPoint: [] }),
        snapshot: { params: { servicePointNumber: 8504414 } },
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(LoadingPointsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should not display current version', () => {
      expect(component.selectedVersion).toBeFalsy();
    });

    it('should save version', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

      component.form.controls.number.setValue(5);
      component.form.controls.designation.setValue('456');

      component.form.controls.validFrom.setValue(
        moment(new Date(2000 - 10 - 1))
      );
      component.form.controls.validTo.setValue(moment(new Date(2099 - 10 - 1)));
      component.save();

      expect(loadingPointService.createLoadingPoint).toHaveBeenCalled();
    });
  });
});
