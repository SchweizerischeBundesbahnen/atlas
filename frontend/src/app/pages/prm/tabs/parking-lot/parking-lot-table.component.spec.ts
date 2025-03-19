import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotTableComponent } from './parking-lot-table.component';
import { ActivatedRoute, Router } from '@angular/router';
import {
  MockAtlasButtonComponent,
  MockTableComponent,
} from '../../../../app.testing.mocks';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { of } from 'rxjs';
import {
  BooleanOptionalAttributeType,
  ParkingLotOverview,
  PersonWithReducedMobilityService,
} from '../../../../api';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslateModule } from '@ngx-translate/core';

const parkingLotOverview: ParkingLotOverview[] = [
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: 'e524381',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: 'e524381',
    id: 1000,
    sloid: 'ch:1:sloid:12345:1',
    validFrom: new Date('2000-01-01'),
    validTo: new Date('2000-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    designation: 'designation',
    additionalInformation: 'additional',
    placesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
    prmPlacesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
    recordingStatus: 'COMPLETE',
  },
];

describe('ParkingLotTableComponent', () => {
  let component: ParkingLotTableComponent;
  let fixture: ComponentFixture<ParkingLotTableComponent>;
  let router: Router;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getParkingLotsOverview']
  );
  personWithReducedMobilityService.getParkingLotsOverview.and.returnValue(
    of(parkingLotOverview)
  );

  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: { stopPointSloid: STOP_POINT.sloid },
        data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] },
      },
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ParkingLotTableComponent, TranslateModule.forRoot()],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityService,
        },
      ],
    }).overrideComponent(ParkingLotTableComponent, {
      remove: { imports: [AtlasButtonComponent, TableComponent] },
      add: { imports: [MockAtlasButtonComponent, MockTableComponent] },
    });
    fixture = TestBed.createComponent(ParkingLotTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load table', () => {
    component.getOverview({ page: 0, size: 10 });

    expect(component.parkingLots.length).toBe(1);
  });

  it('should navigate on table click', () => {
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.getOverview({ page: 0, size: 10 });

    component.rowClicked(component.parkingLots[0]);
    expect(router.navigate).toHaveBeenCalled();
  });
});
