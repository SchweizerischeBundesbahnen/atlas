import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToiletComponent } from './toilet.component';
import {
  MockAtlasButtonComponent,
  MockTableComponent,
} from '../../../../app.testing.mocks';
import { ActivatedRoute } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import {
  PersonWithReducedMobilityService,
  StandardAttributeType,
  ToiletOverview,
} from '../../../../api';
import { of } from 'rxjs';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';

const toiletOverview: ToiletOverview[] = [
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
    wheelchairToilet: StandardAttributeType.Yes,
    recordingStatus: 'COMPLETE',
  },
];

describe('Toilet Component', () => {
  let component: ToiletComponent;
  let fixture: ComponentFixture<ToiletComponent>;
  const activatedRouteMock = {
    parent: {
      snapshot: {
        data: {
          stopPoints: [STOP_POINT],
          servicePoints: [BERN_WYLEREGG],
        },
        params: {
          stopPointSloid: STOP_POINT.sloid,
        },
      },
    },
  };

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getToiletOverview']
  );

  personWithReducedMobilityService.getToiletOverview.and.returnValue(
    of(toiletOverview)
  );

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ToiletComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityService,
        },
      ],
    }).overrideComponent(ToiletComponent, {
      remove: { imports: [AtlasButtonComponent, TableComponent] },
      add: { imports: [MockAtlasButtonComponent, MockTableComponent] },
    });
    fixture = TestBed.createComponent(ToiletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get overview', () => {
    const tablePagination: TablePagination = {
      page: 1,
      size: 10,
      sort: 'designation,asc',
    };
    component.getOverview(tablePagination);

    expect(component.totalCount).toBe(1);
  });
});
