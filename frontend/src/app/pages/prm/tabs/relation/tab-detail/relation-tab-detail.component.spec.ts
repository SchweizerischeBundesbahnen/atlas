import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RelationTabDetailComponent} from './relation-tab-detail.component';
import {AppTestingModule} from '../../../../../app.testing.module';
import {ActivatedRoute} from '@angular/router';
import {MockAtlasButtonComponent, MockSelectComponent} from '../../../../../app.testing.mocks';
import {STOP_POINT} from '../../../util/stop-point-test-data.spec';
import {BERN_WYLEREGG} from '../../../../../../test/data/service-point';
import {BERN_WYLEREGG_TRAFFIC_POINTS} from "../../../../../../test/data/traffic-point-element";
import {DetailPageContentComponent} from "../../../../../core/components/detail-page-content/detail-page-content.component";
import {PersonWithReducedMobilityService, ReadReferencePointVersion} from "../../../../../api";
import {of} from "rxjs";

const referencePointOverview: ReadReferencePointVersion[] = [
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
    mainReferencePoint: true,
    referencePointType: 'PLATFORM',
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
  },
];

describe('RelationTabDetailComponent', () => {
  let component: RelationTabDetailComponent;
  let fixture: ComponentFixture<RelationTabDetailComponent>;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getReferencePointsOverview'],
  );

  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: {
          sloid: BERN_WYLEREGG_TRAFFIC_POINTS[0].sloid,
          stopPointSloid: STOP_POINT.sloid
        },
        data: {stopPoint: [STOP_POINT], servicePoints: [BERN_WYLEREGG]}
      }
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelationTabDetailComponent, MockAtlasButtonComponent, MockSelectComponent, DetailPageContentComponent],
      imports: [AppTestingModule],
      providers: [
        {provide: ActivatedRoute, useValue: activatedRouteMock},
        {provide: PersonWithReducedMobilityService, useValue: personWithReducedMobilityService},
      ],
    });
    fixture = TestBed.createComponent(RelationTabDetailComponent);
    component = fixture.componentInstance;

    personWithReducedMobilityService.getReferencePointsOverview.and.returnValue(of([]));
    fixture.detectChanges();
  });

  it('should init relation tab for complete variant', () => {
    personWithReducedMobilityService.getReferencePointsOverview.and.returnValue(of(referencePointOverview));
    fixture.detectChanges();

    component.ngOnInit();

    expect(component).toBeTruthy();

    expect(personWithReducedMobilityService.getReferencePointsOverview).toHaveBeenCalled();
    expect(component.elementSloid).toBe('ch:1:sloid:89008:0:1');
    expect(component.selectedReferencePointSloid).toBe('ch:1:sloid:12345:1');
  });

  it('should init relation tab for complete variant without reference points', () => {
    component.ngOnInit();

    expect(personWithReducedMobilityService.getReferencePointsOverview).toHaveBeenCalled();
    expect(component.elementSloid).toBe('ch:1:sloid:89008:0:1');
    expect(component.selectedReferencePointSloid).toBeUndefined();
  });
});
