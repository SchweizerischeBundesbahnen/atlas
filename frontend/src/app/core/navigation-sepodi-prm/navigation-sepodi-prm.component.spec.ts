import { beforeEach, describe, expect, it, Mocked, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavigationSepodiPrmComponent, TargetPageType } from './navigation-sepodi-prm.component';
import { AppTestingModule } from '../../app.testing.module';
import { Router } from '@angular/router';
import { ReadServicePointVersion, ReadStopPointVersion } from '../../api';
import { BERN_WYLEREGG } from '../../../test/data/service-point';
import { of } from 'rxjs';
import { STOP_POINT } from '../../pages/prm/util/stop-point-test-data';
import { ServicePointService } from '../../api/service/sepodi/service-point.service';
import { StopPointService } from '../../api/service/prm/stop-point/stop-point.service';
import { inputBinding, signal } from '@angular/core';

describe('NavigationSepodiPrmComponent', () => {
  let component: NavigationSepodiPrmComponent;
  let fixture: ComponentFixture<NavigationSepodiPrmComponent>;

  type RouterMock = Mocked<Pick<Router, 'navigateByUrl'>>;
  type StopPointServiceMock = Mocked<Pick<StopPointService, 'getStopPointVersions'>>;
  type ServicePointServiceMock = Mocked<Pick<ServicePointService, 'getServicePointVersions'>>;

  let routerMock: RouterMock;
  let stopPointServiceMock: StopPointServiceMock;
  let servicePointServiceMock: ServicePointServiceMock;
  let targetPageInput: ReturnType<typeof signal<TargetPageType>>;
  let sloidInput: ReturnType<typeof signal<string | undefined>>;
  let numberInput: ReturnType<typeof signal<number | undefined>>;
  let parentSloidInput: ReturnType<typeof signal<string | undefined>>;

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

    const targetPageInputName: keyof NavigationSepodiPrmComponent = 'targetPage';
    const sloidInputName: keyof NavigationSepodiPrmComponent = 'sloid';
    const numberInputName: keyof NavigationSepodiPrmComponent = 'number';
    const parentSloidInputName: keyof NavigationSepodiPrmComponent = 'parentSloid';
    targetPageInput = signal('stop-point');
    sloidInput = signal(undefined);
    numberInput = signal(undefined);
    parentSloidInput = signal(undefined);
    fixture = TestBed.createComponent(NavigationSepodiPrmComponent, {
      bindings: [
        inputBinding(targetPageInputName, targetPageInput),
        inputBinding(sloidInputName, sloidInput),
        inputBinding(numberInputName, numberInput),
        inputBinding(parentSloidInputName, parentSloidInput),
      ],
    });
    component = fixture.componentInstance;
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
      sloidInput.set('ch:1:sloid:89008');
      fixture.detectChanges();
      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(false);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${sloidInput()}/stop-point`
      );
    });

    it('should navigate to the correct URL when targetPage is service point', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      numberInput.set(8589008);
      targetPageInput.set('service-point');
      fixture.detectChanges();
      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(true);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/service-point-directory/service-points/${numberInput()}/service-point`
      );
    });

    it('should navigate to the correct URL when targetPage is traffic point table', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      numberInput.set(8589008);
      targetPageInput.set('traffic-point-table');
      fixture.detectChanges();
      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(true);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/service-point-directory/service-points/${numberInput()}/traffic-point-elements`
      );
    });

    it('should navigate to the correct URL when targetPage is traffic point detail', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      sloidInput.set('ch:1:sloid:89008');
      numberInput.set(8589008);
      targetPageInput.set('traffic-point-detail');
      fixture.detectChanges();
      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(true);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/service-point-directory/service-points/${numberInput()}/traffic-point-elements/${sloidInput()}`
      );
    });

    it('should navigate to the correct URL when targetPage is platform table', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      sloidInput.set('ch:1:sloid:89008');
      targetPageInput.set('platform-table');
      fixture.detectChanges();
      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(false);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${sloidInput()}/platforms`
      );
    });

    it('should navigate to the correct URL when targetPage is platform detail', () => {
      stopPointServiceMock.getStopPointVersions.mockReturnValue(of([STOP_POINT]));
      parentSloidInput.set('ch:1:sloid:89008');
      sloidInput.set('ch:1:sloid:89008:0:1');
      targetPageInput.set('platform-detail');
      fixture.detectChanges();
      component.init();
      component.navigate();

      expect(component.isTargetViewSepodi).toBe(false);
      expect(routerMock.navigateByUrl).toHaveBeenCalledExactlyOnceWith(
        `/prm-directory/stop-points/${parentSloidInput()}/platforms/${sloidInput()}/detail`
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
