import {ComponentFixture, TestBed} from '@angular/core/testing';
import {RelationTabDetailComponent} from './relation-tab-detail.component';
import {AppTestingModule} from '../../../../../app.testing.module';
import {ActivatedRoute} from '@angular/router';
import {
  MockAtlasButtonComponent,
  MockSelectComponent,
  MockSwitchVersionComponent,
  MockUserDetailInfoComponent,
} from '../../../../../app.testing.mocks';
import {STOP_POINT} from '../../../util/stop-point-test-data.spec';
import {BERN_WYLEREGG} from '../../../../../../test/data/service-point';
import {BERN_WYLEREGG_TRAFFIC_POINTS,} from '../../../../../../test/data/traffic-point-element';
import {DetailPageContentComponent} from '../../../../../core/components/detail-page-content/detail-page-content.component';
import {PersonWithReducedMobilityService, ReadReferencePointVersion, ReadRelationVersion,} from '../../../../../api';
import {of} from 'rxjs';
import {DetailFooterComponent} from '../../../../../core/components/detail-footer/detail-footer.component';
import {AtlasSpacerComponent} from '../../../../../core/components/spacer/atlas-spacer.component';
import {ValidityService} from "../../../../sepodi/validity/validity.service";
import moment from "moment";

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

const relations: ReadRelationVersion[] = [
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: 'e524381',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: 'e524381',
    id: 1000,
    validFrom: new Date('2000-01-01'),
    validTo: new Date('2000-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    referencePointSloid: 'ch:1:sloid:12345:1',
    elementSloid: 'ch:1:sloid:89008:0:1',
    tactileVisualMarks: 'TO_BE_COMPLETED',
    contrastingAreas: 'TO_BE_COMPLETED',
    stepFreeAccess: 'TO_BE_COMPLETED',
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
    referencePointElementType: 'PLATFORM',
  },
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: 'e524381',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: 'e524381',
    id: 1001,
    validFrom: new Date('2001-01-01'),
    validTo: new Date('2001-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    referencePointSloid: 'ch:1:sloid:12345:1',
    elementSloid: 'ch:1:sloid:89008:0:1',
    tactileVisualMarks: 'TO_BE_COMPLETED',
    contrastingAreas: 'TO_BE_COMPLETED',
    stepFreeAccess: 'TO_BE_COMPLETED',
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
    referencePointElementType: 'PLATFORM',
  },
];
describe('RelationTabDetailComponent', () => {
  let component: RelationTabDetailComponent;
  let fixture: ComponentFixture<RelationTabDetailComponent>;

  let personWithReducedMobilityService = jasmine.createSpyObj('personWithReducedMobilityService', [
    'getReferencePointsOverview',
    'getRelationsBySloid',
    'updateRelation',
  ]);

  const activatedRouteMock = {
    parent: {
      snapshot: {
        params: {
          sloid: BERN_WYLEREGG_TRAFFIC_POINTS[0].sloid,
          stopPointSloid: STOP_POINT.sloid,
        },
        data: { stopPoint: [STOP_POINT], servicePoint: [BERN_WYLEREGG] },
      },
    },
  };

  beforeEach(() => {
    personWithReducedMobilityService = jasmine.createSpyObj('personWithReducedMobilityService', [
      'getReferencePointsOverview',
      'getRelationsBySloid',
      'updateRelation',
    ]);
    personWithReducedMobilityService.getReferencePointsOverview.and.returnValue(of([]));
    personWithReducedMobilityService.getRelationsBySloid.and.returnValue(of([]));
    personWithReducedMobilityService.updateRelation.and.returnValue(of());
    TestBed.configureTestingModule({
    imports: [AppTestingModule, RelationTabDetailComponent,
        AtlasSpacerComponent,
        MockAtlasButtonComponent,
        MockSelectComponent,
        MockSwitchVersionComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
        MockUserDetailInfoComponent],
    providers: [
        ValidityService,
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: PersonWithReducedMobilityService, useValue: personWithReducedMobilityService },
    ],
});
    fixture = TestBed.createComponent(RelationTabDetailComponent);
    component = fixture.componentInstance;
  });

  it('should init relation tab for complete variant', () => {
    personWithReducedMobilityService.getReferencePointsOverview.and.returnValue(
      of(referencePointOverview),
    );
    personWithReducedMobilityService.getRelationsBySloid.and.returnValue(of(relations));
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(personWithReducedMobilityService.getReferencePointsOverview).toHaveBeenCalled();
    expect(personWithReducedMobilityService.getRelationsBySloid).toHaveBeenCalled();
    expect(component.elementSloid).toBe('ch:1:sloid:89008:0:1');
    expect(component.selectedReferencePointSloid).toBe('ch:1:sloid:12345:1');
    expect(component.parentServicePointSloid).toBe('ch:1:sloid:89008');
    expect(component.businessOrganisations).toEqual(['ch:1:sboid:100626']);
    expect(component.form).toBeDefined();
    expect(component.currentRelationId).toBe(1001);
    expect(component.selectedRelationVersion).toBe(2);
  });

  it('should init relation tab for complete variant without reference points', () => {
    fixture.detectChanges();

    expect(personWithReducedMobilityService.getReferencePointsOverview).toHaveBeenCalled();
    expect(personWithReducedMobilityService.getRelationsBySloid).not.toHaveBeenCalled();
    expect(component.elementSloid).toBe('ch:1:sloid:89008:0:1');
    expect(component.parentServicePointSloid).toBe('ch:1:sloid:89008');
    expect(component.businessOrganisations).toEqual(['ch:1:sboid:100626']);
    expect(component.selectedReferencePointSloid).toBeUndefined();
  });

  it('should switch reference point', () => {
    // triggers ngOnInit()
    fixture.detectChanges();
    personWithReducedMobilityService.getRelationsBySloid.and.returnValue(of([relations[0]]));

    component.referencePointChanged({
      source: undefined!,
      value: 'ch:1:sloid:12345:1',
    });
    fixture.detectChanges();

    expect(component.selectedReferencePointSloid).toBe('ch:1:sloid:12345:1');
    expect(personWithReducedMobilityService.getRelationsBySloid).toHaveBeenCalledTimes(1);
    expect(component.form).toBeDefined();
    expect(component.currentRelationId).toBe(1000);
    expect(component.selectedRelationVersion).toBe(1);
  });

  it('should change relation version correctly', () => {
    personWithReducedMobilityService.getReferencePointsOverview.and.returnValue(
      of(referencePointOverview),
    );
    personWithReducedMobilityService.getRelationsBySloid.and.returnValue(of(relations));
    fixture.detectChanges();

    component.versionChanged(relations[0], 0);

    expect(component.selectedRelationVersion).toBe(1);
    expect(component.currentRelationId).toBe(1000);
  });

  it('should save valid form', () => {
    personWithReducedMobilityService.getReferencePointsOverview.and.returnValue(
      of(referencePointOverview),
    );
    personWithReducedMobilityService.getRelationsBySloid.and.returnValue(of(relations));
    fixture.detectChanges();

    component.toggleEdit();
    expect(component.editing).toBeTrue();

    component.form?.controls.validFrom.setValue(moment('2000-01-02'));
    component.save();

    expect(component.editing).toBeFalse();
    expect(personWithReducedMobilityService.updateRelation).toHaveBeenCalledTimes(1);
  });
});
