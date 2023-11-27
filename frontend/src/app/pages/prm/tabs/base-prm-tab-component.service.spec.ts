import { TestBed } from '@angular/core/testing';

import { BasePrmTabComponentService } from './base-prm-tab-component.service';
import { Data } from '@angular/router';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import { STOP_POINT, STOP_POINT_COMPLETE } from '../util/stop-point-test-data';
import { AppTestingModule } from '../../../app.testing.module';
import { RouterTestingModule } from '@angular/router/testing';
import { StopPointDetailComponent } from './stop-point/detail/stop-point-detail.component';

describe('BasePrmTabComponentService', () => {
  let service: BasePrmTabComponentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        RouterTestingModule.withRoutes([
          {
            path: 'prm-directory/stop-points/ch:1:sloid:89008/stop-point',
            component: StopPointDetailComponent,
          },
        ]),
      ],
    });
    service = TestBed.inject(BasePrmTabComponentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not showCurrentTab when stopPoint does not exists', () => {
    //given
    spyOn(service, 'redirectToStopPoint');
    const data: Data = { servicePoints: [BERN_WYLEREGG], stopPoints: [] };
    //when
    service.showCurrentTab(data);
    // then
    expect(service.isStopPointExisting).toBeFalsy();
    expect(service.redirectToStopPoint).toHaveBeenCalled();
  });

  it('should not showCurrentTab when stopPoint is Reduced on Complete Tab', () => {
    //given
    spyOn(service, 'redirectToStopPoint');
    spyOn(service, 'canShowTab').and.returnValue(true);
    const data: Data = { servicePoints: [BERN_WYLEREGG], stopPoints: [STOP_POINT] };
    //when
    service.showCurrentTab(data);
    // then
    expect(service.isStopPointExisting).toBeTruthy();
    expect(service.redirectToStopPoint).toHaveBeenCalled();
  });

  it('should showCurrentTab when stopPoint is Complete', () => {
    //given
    spyOn(service, 'redirectToStopPoint');
    const data: Data = { servicePoints: [BERN_WYLEREGG], stopPoints: [STOP_POINT_COMPLETE] };
    //when
    service.showCurrentTab(data);
    // then
    expect(service.isStopPointExisting).toBeTruthy();
    expect(service.redirectToStopPoint).not.toHaveBeenCalled();
  });
});
