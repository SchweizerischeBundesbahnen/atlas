import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { TrafficPointElementsSidePanelComponent } from './traffic-point-elements-side-panel.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { of } from 'rxjs';
import { ActivatedRouteMockType } from '../../../app.testing.mocks';
import { TranslatePipe } from '@ngx-translate/core';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { ServicePointService } from '../../../api/service/sepodi/service-point.service';
import { SectorMapService } from '../map/sector-map.service';

describe('TrafficPointElementsSidePanelComponent', () => {
  let component: TrafficPointElementsSidePanelComponent;
  let fixture: ComponentFixture<TrafficPointElementsSidePanelComponent>;

  const servicePointService = jasmine.createSpyObj(['getServicePointVersions']);
  servicePointService.getServicePointVersions.and.returnValue(
    of([BERN_WYLEREGG])
  );
  const sectorMapService = jasmine.createSpyObj<SectorMapService>([
    'displaySectorsOnMap',
    'clearDisplayedSectors',
  ]);

  beforeEach(() => {
    const activatedRouteMock = {
      snapshot: {
        params: {
          servicePointNumber: 8507000,
        },
      },
      data: of({
        trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
        isTrafficPointArea: false,
      }),
    };
    setupTestBed(activatedRouteMock);
    fixture = TestBed.createComponent(TrafficPointElementsSidePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display current designationOperational and validity', () => {
    expect(component.selectedVersion).toBeTruthy();

    expect(component.selectedVersion.designationOperational).toEqual('1');
    expect(component.maxValidity.validFrom).toEqual(new Date('2019-07-22'));
    expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));
  });

  it('should init selected servicepoint', fakeAsync(() => {
    expect(component.servicePointName).toBeTruthy();

    expect(servicePointService.getServicePointVersions).toHaveBeenCalled();
  }));

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    return TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        TrafficPointElementsSidePanelComponent,
        DetailPageContainerComponent,
      ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: ServicePointService, useValue: servicePointService },
        { provide: SectorMapService, useValue: sectorMapService },
        TranslatePipe,
      ],
    }).compileComponents();
  }
});
