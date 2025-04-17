import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformDetailPanelComponent } from './platform-detail-panel.component';
import { STOP_POINT } from '../../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../../../test/data/traffic-point-element';
import { AppTestingModule } from '../../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { SplitServicePointNumberPipe } from '../../../../../core/search-service-point/split-service-point-number.pipe';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailWithRelationTabComponent } from '../../relation/tab/detail-with-relation-tab.component';
import { of } from 'rxjs';

describe('PlatformDetailPanelComponent', () => {
  let component: PlatformDetailPanelComponent;
  let fixture: ComponentFixture<PlatformDetailPanelComponent>;

  const activatedRouteMock = {
    data: of({
      stopPoint: [STOP_POINT],
      servicePoint: [BERN_WYLEREGG],
      platform: [],
      trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
    }),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        PlatformDetailPanelComponent,
        DetailWithRelationTabComponent,
        DetailPageContainerComponent,
      ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        TranslatePipe,
        SplitServicePointNumberPipe,
      ],
    });
    fixture = TestBed.createComponent(PlatformDetailPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should init', () => {
    expect(component).toBeTruthy();

    expect(component.isNew).toBeTrue();
    expect(component.isReduced).toBeTrue();
    expect(component.selectedVersion).toBeUndefined();
  });
});
