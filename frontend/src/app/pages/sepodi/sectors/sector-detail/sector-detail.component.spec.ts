import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SectorDetailComponent } from './sector-detail.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { of } from 'rxjs';
import { ActivatedRouteMockType } from '../../../app.testing.mocks';
import { TranslatePipe } from '@ngx-translate/core';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';
import { DetailPageContainerComponent } from '../../../core/components/detail-page-container/detail-page-container.component';
import { SectorMapService } from '../map/sector-map.service';

describe('SectorDetailComponent', () => {
  let component: SectorDetailComponent;
  let fixture: ComponentFixture<SectorDetailComponent>;

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
        servicePoint: [BERN_WYLEREGG],
        isTrafficPointArea: false,
      }),
    };
    setupTestBed(activatedRouteMock);
    fixture = TestBed.createComponent(SectorDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display current designationOperational and validity', () => {
    expect(component.selectedVersion).toBeTruthy();

    expect(component.selectedVersion.designationOperational).toEqual('1');
    expect(component.maxValidity.validFrom).toEqual(new Date('2019-07-22'));
    expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));

    expect(component.servicePointDesignationOfficial).toBe('Bern, Wyleregg');
  });

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    return TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        SectorDetailComponent,
        DetailPageContainerComponent,
      ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SectorMapService, useValue: sectorMapService },
        TranslatePipe,
      ],
    }).compileComponents();
  }
});
