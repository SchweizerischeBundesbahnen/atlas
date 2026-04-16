import { beforeEach, describe, expect, it, Mocked, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavigationSepodiPrmComponent } from './navigation-sepodi-prm.component';
import { AppTestingModule } from '../../app.testing.module';
import { Router } from '@angular/router';
import { ReadServicePointVersion, ReadStopPointVersion } from '../../api';
import { BERN_WYLEREGG } from '../../../test/data/service-point';
import { of } from 'rxjs';
import { STOP_POINT } from '../../pages/prm/util/stop-point-test-data';
import { ServicePointService } from '../../api/service/sepodi/service-point.service';
import { StopPointService } from '../../api/service/prm/stop-point/stop-point.service';

describe('NavigationSepodiPrmComponent', () => {
  type RouterMock = Mocked<Pick<Router, 'navigateByUrl'>>;
  type StopPointServiceMock = Mocked<Pick<StopPointService, 'getStopPointVersions'>>;
  type ServicePointServiceMock = Mocked<Pick<ServicePointService, 'getServicePointVersions'>>;

  let component: NavigationSepodiPrmComponent;
  let fixture: ComponentFixture<NavigationSepodiPrmComponent>;
  let routerMock: RouterMock;
  let stopPointServiceMock: StopPointServiceMock;
  let servicePointServiceMock: ServicePointServiceMock;

  beforeEach(() => {
    stopPointServiceMock = {
      getStopPointVersions: vi.fn().mockName('StopPointService.getStopPointVersions'),
    };
    servicePointServiceMock = {
      getServicePointVersions: vi.fn().mockName('ServicePointService.getServicePointVersions'),
    };
    routerMock = {
      navigateByUrl: vi.fn().mockName('Router.navigateByUrl'),
    };

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: StopPointService, useValue: stopPointServiceMock },
        { provide: ServicePointService, useValue: servicePointServiceMock },
      ],
    });

    fixture = TestBed.createComponent(NavigationSepodiPrmComponent);
    component = fixture.componentInstance;
    component.targetPage = 'stop-point';
    servicePointServiceMock.getServicePointVersions.mockReturnValue(of([BERN_WYLEREGG]));
  });

  describe('With Component Init', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to the correct URL when targetPage is stop point', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      component.sloid = 'ch:1:sloid:89008';

      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(false);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${component.sloid}/stop-point`
      );
    });

    it('should navigate to the correct URL when targetPage is service point', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      component.number = 8589008;
      component.targetPage = 'service-point';

      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(true);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/service-point-directory/service-points/${component.number}/service-point`
      );
    });

    it('should navigate to the correct URL when targetPage is traffic point table', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      component.number = 8589008;
      component.targetPage = 'traffic-point-table';

      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(true);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/service-point-directory/service-points/${component.number}/traffic-point-elements`
      );
    });

    it('should navigate to the correct URL when targetPage is traffic point detail', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      component.sloid = 'ch:1:sloid:89008';
      component.number = 8589008;
      component.targetPage = 'traffic-point-detail';

      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(true);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/service-point-directory/service-points/${component.number}/traffic-point-elements/${component.sloid}`
      );
    });

    it('should navigate to the correct URL when targetPage is platform table', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      component.sloid = 'ch:1:sloid:89008';
      component.targetPage = 'platform-table';

      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(false);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${component.sloid}/platforms`
      );
    });

    it('should navigate to the correct URL when targetPage is platform detail', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      component.parentSloid = 'ch:1:sloid:89008';
      component.sloid = 'ch:1:sloid:89008:0:1';
      component.targetPage = 'platform-detail';

      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(false);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${component.parentSloid}/platforms/${component.sloid}/detail`
      );
    });

    it('should navigate to create stop point when the stop point returns an empty array', () => {
      const sloid = BERN_WYLEREGG.sloid!;
      const mockResponse: ReadStopPointVersion[] = [];

      stopPointServiceMock.getStopPointVersions.mockReturnValue(of(mockResponse));

      component.checkStopPointExists(sloid);

      expect(stopPointServiceMock.getStopPointVersions).toHaveBeenCalledExactlyOnceWith(sloid);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${sloid}/stop-point`
      );
    });
  });

  describe('Without Component Init', () => {
    it('should set isSwissServicePoint to true when the service point is in Switzerland', () => {
      const number = 8589008;
      const mockResponse: ReadServicePointVersion[] = [BERN_WYLEREGG];

      servicePointServiceMock.getServicePointVersions.mockReturnValue(of(mockResponse));

      component.checkServicePointIsLocatedInSwitzerland(number);

      expect(servicePointServiceMock.getServicePointVersions).toHaveBeenCalledExactlyOnceWith(number);
      expect(component.isSwissServicePoint).toBe(true);
    });

    it('should set isStopPoint to true when the service point has version with stopPoint true', () => {
      const number = 8589008;
      const mockResponse: ReadServicePointVersion[] = [BERN_WYLEREGG];

      servicePointServiceMock.getServicePointVersions.mockReturnValue(of(mockResponse));

      component.checkServicePointIsLocatedInSwitzerland(number);

      expect(servicePointServiceMock.getServicePointVersions).toHaveBeenCalledExactlyOnceWith(number);
      expect(component.isSwissServicePoint).toBe(true);
      expect(component.isStopPoint).toBe(true);
    });
  });
});
