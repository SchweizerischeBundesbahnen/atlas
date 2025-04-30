import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactPointTableComponent } from './contact-point-table.component';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import {
  MockAtlasButtonComponent,
  MockTableComponent,
} from '../../../../app.testing.mocks';
import { ActivatedRoute, Router } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { of } from 'rxjs';
import {
  ContactPointOverview,
  ContactPointType,
  PersonWithReducedMobilityService,
  StandardAttributeType,
} from '../../../../api';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';

const contactPointOverview: ContactPointOverview[] = [
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
    inductionLoop: StandardAttributeType.ToBeCompleted,
    openingHours: 'openingHours',
    wheelchairAccess: StandardAttributeType.ToBeCompleted,
    type: ContactPointType.InformationDesk,
    recordingStatus: 'INCOMPLETE',
  },
];

describe('ContactPointTableComponent', () => {
  let component: ContactPointTableComponent;
  let fixture: ComponentFixture<ContactPointTableComponent>;
  let router: Router;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getContactPointOverview']
  );
  personWithReducedMobilityService.getContactPointOverview.and.returnValue(
    of(contactPointOverview)
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
      imports: [ContactPointTableComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityService,
        },
      ],
    }).overrideComponent(ContactPointTableComponent, {
      remove: { imports: [AtlasButtonComponent, TableComponent] },
      add: { imports: [MockAtlasButtonComponent, MockTableComponent] },
    });
    fixture = TestBed.createComponent(ContactPointTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load table', () => {
    component.getOverview({ page: 0, size: 10 });

    expect(component.contactPoints.length).toBe(1);
  });

  it('should navigate on table click', () => {
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.getOverview({ page: 0, size: 10 });

    component.rowClicked(component.contactPoints[0]);
    expect(router.navigate).toHaveBeenCalled();
  });
});
