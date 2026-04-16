import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { BasePrmTabComponentService } from './base-prm-tab-component.service';
import { Data } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { RouterTestingModule } from '@angular/router/testing';
import { StopPointDetailComponent } from './stop-point/detail/stop-point-detail.component';
import { STOP_POINT, STOP_POINT_COMPLETE } from '../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';

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
    const redirectToStopPointSpy = vi.spyOn(service, 'redirectToStopPoint').mockImplementation(() => {});
    const data: Data = { servicePoints: [BERN_WYLEREGG], stopPoints: [] };

    service.showCurrentTab(data);

    expect(service.isStopPointExisting).toBeFalsy();
    expect(redirectToStopPointSpy).toHaveBeenCalled();
  });

  it('should not showCurrentTab when stopPoint is Reduced on Complete Tab', () => {
    const redirectToStopPointSpy = vi.spyOn(service, 'redirectToStopPoint').mockImplementation(() => {});
    vi.spyOn(service, 'canShowTab').mockReturnValue(true);
    const data: Data = {
      servicePoints: [BERN_WYLEREGG],
      stopPoints: [STOP_POINT],
    };

    service.showCurrentTab(data);

    expect(service.isStopPointExisting).toBeTruthy();
    expect(redirectToStopPointSpy).toHaveBeenCalled();
  });

  it('should showCurrentTab when stopPoint is Complete', () => {
    const redirectToStopPointSpy = vi.spyOn(service, 'redirectToStopPoint').mockImplementation(() => {});
    const data: Data = {
      servicePoints: [BERN_WYLEREGG],
      stopPoints: [STOP_POINT_COMPLETE],
    };

    service.showCurrentTab(data);

    expect(service.isStopPointExisting).toBeTruthy();
    expect(redirectToStopPointSpy).not.toHaveBeenCalled();
  });
});
