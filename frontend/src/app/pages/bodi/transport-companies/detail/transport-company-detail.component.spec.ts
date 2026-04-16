import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusinessOrganisation, TransportCompany, TransportCompanyBoRelation } from '../../../../api';
import { TransportCompanyDetailComponent } from './transport-company-detail.component';
import moment from 'moment';
import { of } from 'rxjs';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { ActivatedRoute } from '@angular/router';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const transportCompany: TransportCompany = {
  id: 1234,
  description: 'SBB',
};

const transportCompanyRelations: TransportCompanyBoRelation[] = [
  {
    id: 1,
    businessOrganisation: {
      said: '100',
      organisationNumber: 50,
      abbreviationDe: 'abbreviation',
      abbreviationIt: 'abbreviation',
      abbreviationEn: 'abbreviation',
      abbreviationFr: 'abbreviation',
      descriptionDe: 'description',
      descriptionEn: 'description',
      descriptionFr: 'description',
      descriptionIt: 'description',
      validFrom: new Date(),
      validTo: new Date(),
    },
    validFrom: new Date(),
    validTo: new Date(),
  },
  {
    id: 2,
    businessOrganisation: {
      said: '101',
      organisationNumber: 51,
      abbreviationDe: 'abbreviation',
      abbreviationIt: 'abbreviation',
      abbreviationEn: 'abbreviation',
      abbreviationFr: 'abbreviation',
      descriptionDe: 'description',
      descriptionEn: 'description',
      descriptionFr: 'description',
      descriptionIt: 'description',
      validFrom: new Date(),
      validTo: new Date(),
    },
    validFrom: new Date(),
    validTo: new Date(),
  },
];

describe('TransportCompanyDetailComponent', () => {
  let component: TransportCompanyDetailComponent;
  let fixture: ComponentFixture<TransportCompanyDetailComponent>;

  let boService: BusinessOrganisationService;
  let transportCompanyRelationInternalService: Mocked<
    Pick<
      TransportCompanyRelationInternalService,
      | 'createTransportCompanyRelation'
      | 'getTransportCompanyBoRelations'
      | 'updateTransportCompanyRelation'
      | 'deleteTransportCompanyRelation'
    >
  >;

  const mockData = [transportCompany, transportCompanyRelations];

  beforeEach(() => {
    transportCompanyRelationInternalService = {
      createTransportCompanyRelation: vi.fn(),
      getTransportCompanyBoRelations: vi.fn(),
      updateTransportCompanyRelation: vi.fn(),
      deleteTransportCompanyRelation: vi.fn(),
    };
    transportCompanyRelationInternalService.createTransportCompanyRelation.mockReturnValue(of({}));
    transportCompanyRelationInternalService.getTransportCompanyBoRelations.mockReturnValue(of([]));
    transportCompanyRelationInternalService.updateTransportCompanyRelation.mockReturnValue(of(undefined));
    transportCompanyRelationInternalService.deleteTransportCompanyRelation.mockReturnValue(of(undefined));

    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { data: { transportCompanyDetail: mockData } },
          },
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        {
          provide: TransportCompanyRelationInternalService,
          useValue: transportCompanyRelationInternalService,
        },
        translateServiceProvider,
      ],
    });

    boService = TestBed.inject(BusinessOrganisationService);
    fixture = TestBed.createComponent(TransportCompanyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(component.transportCompany).toEqual({
      id: 1234,
      description: 'SBB',
    });
    expect(component.transportCompanyRelations).toEqual(transportCompanyRelations);
  });

  it('test selectOption function', () => {
    expect(
      component.selectOption({
        organisationNumber: 5,
        abbreviationDe: 'testAbbreviation',
        descriptionDe: 'testDescription',
      } as BusinessOrganisation)
    ).toBe('5 - testAbbreviation - testDescription');
  });

  it('should call getAllBusinessOrganisations with correct params', () => {
    vi.spyOn(boService, 'getAllBusinessOrganisations').mockReturnValue(
      of({
        objects: [],
        totalCount: 0,
      })
    );

    component.getBusinessOrganisations('testSearchString');
    expect(boService.getAllBusinessOrganisations).toHaveBeenCalledExactlyOnceWith(
      ['testSearchString'],
      undefined,
      undefined,
      undefined,
      undefined,
      100
    );
  });

  it('should call createTransportCompanyRelation and reloadRelations', () => {
    component.editMode = true;

    component.form.setValue({
      businessOrganisation: {
        sboid: 'ch:1:sboid:100500',
      } as BusinessOrganisation,
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });

    expect(component.form.valid).toBe(true);

    component.save();

    expect(component.form.untouched).toBe(true);
    expect(component.editMode).toBe(false);
    expect(transportCompanyRelationInternalService.createTransportCompanyRelation).toHaveBeenCalledExactlyOnceWith({
      transportCompanyId: 1234,
      sboid: 'ch:1:sboid:100500',
      validFrom: moment('2020-05-05').toDate(),
      validTo: moment('2021-05-05').toDate(),
    });
    expect(transportCompanyRelationInternalService.getTransportCompanyBoRelations).toHaveBeenCalledExactlyOnceWith(
      1234
    );
  });

  it('should call updateTransportCompanyRelation and reloadRelations', () => {
    component.editMode = true;
    component.isUpdateRelationSelected = true;
    component.relationId = 1;

    component.form.setValue({
      businessOrganisation: {
        sboid: 'ch:1:sboid:100500',
      } as BusinessOrganisation,
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });

    expect(component.form.valid).toBe(true);

    component.save();

    expect(component.form.untouched).toBe(true);
    expect(component.editMode).toBe(false);
    expect(component.isUpdateRelationSelected).toBe(false);
    expect(transportCompanyRelationInternalService.updateTransportCompanyRelation).toHaveBeenCalledExactlyOnceWith({
      id: 1,
      validFrom: moment('2020-05-05').toDate(),
      validTo: moment('2021-05-05').toDate(),
    });
    expect(transportCompanyRelationInternalService.getTransportCompanyBoRelations).toHaveBeenCalledExactlyOnceWith(
      1234
    );
  });

  it('should call deleteTransportCompanyRelation and reload relations', () => {
    component.selectedTransportCompanyRelationIndex = 0;
    component.deleteRelation();
    expect(transportCompanyRelationInternalService.deleteTransportCompanyRelation).toHaveBeenCalledExactlyOnceWith(1);
    expect(transportCompanyRelationInternalService.getTransportCompanyBoRelations).toHaveBeenCalledExactlyOnceWith(
      1234
    );
  });
});
