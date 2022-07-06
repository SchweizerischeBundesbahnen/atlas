import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  BusinessOrganisation,
  BusinessOrganisationsService,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyRelationsService,
} from '../../../../api';
import { TransportCompanyDetailComponent } from './transport-company-detail.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { AuthService } from '../../../../core/auth/auth.service';
import { RelationComponent } from '../../../../core/components/relation/relation.component';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import moment from 'moment';
import { of } from 'rxjs';

const transportCompany: TransportCompany = {
  id: 1234,
  description: 'SBB',
};

const transportCompanyRelations: TransportCompanyBoRelation[] = [
  {
    id: 1,
    description: 'Schweizerische Bundesbahnen',
    organisationNumber: 50,
  },
  {
    id: 2,
    description: 'BLS',
    organisationNumber: 77,
  },
];

let component: TransportCompanyDetailComponent;
let fixture: ComponentFixture<TransportCompanyDetailComponent>;

let boService: BusinessOrganisationsService;

describe('TransportCompanyDetailComponent', () => {
  const mockData = [transportCompany, transportCompanyRelations];

  beforeEach(() => {
    setupTestBed(mockData);

    fixture = TestBed.createComponent(TransportCompanyDetailComponent);
    boService = TestBed.inject(BusinessOrganisationsService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(component.transportCompany).toEqual({
      id: 1234,
      description: 'SBB',
    });
    expect(component.transportCompanyRelations).toEqual([
      {
        id: 1,
        description: 'Schweizerische Bundesbahnen',
        organisationNumber: 50,
      },
      {
        id: 2,
        description: 'BLS',
        organisationNumber: 77,
      },
    ]);
  });

  it('test selectOption function', () => {
    expect(
      component.selectOption({
        organisationNumber: 5,
        abbreviationDe: 'testAbbreviation',
      } as BusinessOrganisation)
    ).toBe('5 (testAbbreviation)');
  });

  it('should call getAllBusinessOrganisations with correct params', () => {
    spyOn(boService, 'getAllBusinessOrganisations').and.callThrough();
    component.getBusinessOrganisations('testSearchString');
    expect(boService.getAllBusinessOrganisations).toHaveBeenCalledOnceWith(
      ['testSearchString'],
      undefined,
      undefined,
      undefined,
      100
    );
  });

  it('should call createTransportCompanyRelation and reloadRelations', () => {
    component.form.setValue({
      businessOrganisation: { sboid: 'ch:1:sboid:100500' } as BusinessOrganisation,
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });

    component.createRelation();

    expect(component.form.touched).toBeTrue();
    expect(
      transportCompanyRelationsServiceSpy.createTransportCompanyRelation
    ).toHaveBeenCalledOnceWith({
      transportCompanyId: 1234,
      sboid: 'ch:1:sboid:100500',
      validFrom: moment('2020-05-05').toDate(),
      validTo: moment('2021-05-05').toDate(),
    });
    expect(
      transportCompanyRelationsServiceSpy.getTransportCompanyRelations
    ).toHaveBeenCalledOnceWith(1234, 'de');
  });

  it('should call deleteTransportCompanyRelation and reload relations', (done) => {
    component.deleteRelation({
      record: { id: 5 },
      callbackFn: () => done(),
    });

    expect(
      transportCompanyRelationsServiceSpy.deleteTransportCompanyRelation
    ).toHaveBeenCalledOnceWith(5);
    expect(
      transportCompanyRelationsServiceSpy.getTransportCompanyRelations
    ).toHaveBeenCalledOnceWith(1234, 'de');
  });
});

let transportCompanyRelationsServiceSpy: any;

function setupTestBed(data: (TransportCompany | TransportCompanyBoRelation[])[]) {
  transportCompanyRelationsServiceSpy = jasmine.createSpyObj('TransportCompanyRelationsService', [
    'createTransportCompanyRelation',
    'getTransportCompanyRelations',
    'deleteTransportCompanyRelation',
  ]);

  transportCompanyRelationsServiceSpy.createTransportCompanyRelation.and.returnValue(of({}));
  transportCompanyRelationsServiceSpy.getTransportCompanyRelations.and.returnValue(of([]));
  transportCompanyRelationsServiceSpy.deleteTransportCompanyRelation.and.returnValue(of({}));

  TestBed.configureTestingModule({
    declarations: [TransportCompanyDetailComponent, RelationComponent, SearchSelectComponent],
    imports: [AppTestingModule],
    providers: [
      {
        provide: MAT_DIALOG_DATA,
        useValue: { transportCompanyDetail: data },
      },
      {
        provide: AuthService,
        useValue: jasmine.createSpyObj<AuthService>(['hasRole']),
      },
      {
        provide: TransportCompanyRelationsService,
        useValue: transportCompanyRelationsServiceSpy,
      },
    ],
  })
    .compileComponents()
    .then();
}
