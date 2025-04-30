import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotDetailPanelComponent } from './parking-lot-detail-panel.component';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { AppTestingModule } from '../../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { SplitServicePointNumberPipe } from '../../../../../core/search-service-point/split-service-point-number.pipe';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { STOP_POINT } from '../../../util/stop-point-test-data.spec';
import { DetailWithRelationTabComponent } from '../../relation/tab/detail-with-relation-tab.component';
import { of } from 'rxjs';
import { PrmDetailPanelComponent } from '../../detail-panel/prm-detail-panel.component';

describe('ParkingLotDetailPanelComponent', () => {
  let component: ParkingLotDetailPanelComponent;
  let fixture: ComponentFixture<ParkingLotDetailPanelComponent>;

  const activatedRouteMock = {
    data: of({
      servicePoint: [BERN_WYLEREGG],
      parkingLot: [],
      stopPoint: [STOP_POINT],
    }),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        ParkingLotDetailPanelComponent,
        PrmDetailPanelComponent,
        DetailWithRelationTabComponent,
        DetailPageContainerComponent,
      ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        TranslatePipe,
        SplitServicePointNumberPipe,
      ],
    });
    fixture = TestBed.createComponent(ParkingLotDetailPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should init', () => {
    expect(component).toBeTruthy();

    expect(component.isNew).toBeTrue();
    expect(component.selectedVersion).toBeUndefined();
  });
});
