import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  BusinessOrganisation,
  BusinessOrganisationsService,
  ContainerBusinessOrganisation,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyRelationsService,
} from '../../../../api';
import { TransportCompanyDetailComponent } from './transport-company-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { AuthService } from '../../../../core/auth/auth.service';
import { RelationComponent } from '../../../../core/components/relation/relation.component';
import moment from 'moment';
import { Observable, of } from 'rxjs';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { Component } from '@angular/core';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { ActivatedRoute } from '@angular/router';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';

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

let component: TransportCompanyDetailComponent;
let fixture: ComponentFixture<TransportCompanyDetailComponent>;

let boService: BusinessOrganisationsService;

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

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
    expect(component.transportCompanyRelations).toEqual(transportCompanyRelations);
  });

  it('test selectOption function', () => {
    expect(
      component.selectOption({
        organisationNumber: 5,
        abbreviationDe: 'testAbbreviation',
        descriptionDe: 'testDescription',
      } as BusinessOrganisation),
    ).toBe('5 - testAbbreviation - testDescription');
  });

  it('should call getAllBusinessOrganisations with correct params', () => {
    (boService.getAllBusinessOrganisations as () => Observable<ContainerBusinessOrganisation>) =
      jasmine
        .createSpy(
          'getAllBusinessOrganisations',
          boService.getAllBusinessOrganisations as () => Observable<ContainerBusinessOrganisation>,
        )
        .and.returnValue(
          of({
            objects: [],
            totalCount: 0,
          }),
        );
    component.getBusinessOrganisations('testSearchString');
    expect(boService.getAllBusinessOrganisations).toHaveBeenCalledOnceWith(
      ['testSearchString'],
      undefined,
      undefined,
      undefined,
      undefined,
      100,
    );
  });

  it('should call createTransportCompanyRelation and reloadRelations', () => {
    component.editMode = true;

    component.form.setValue({
      businessOrganisation: { sboid: 'ch:1:sboid:100500' } as BusinessOrganisation,
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });

    expect(component.form.valid).toBeTrue();

    component.save();

    expect(component.form.untouched).toBeTrue();
    expect(component.editMode).toBeFalse();
    expect(
      transportCompanyRelationsServiceSpy.createTransportCompanyRelation,
    ).toHaveBeenCalledOnceWith({
      transportCompanyId: 1234,
      sboid: 'ch:1:sboid:100500',
      validFrom: moment('2020-05-05').toDate(),
      validTo: moment('2021-05-05').toDate(),
    });
    expect(
      transportCompanyRelationsServiceSpy.getTransportCompanyRelations,
    ).toHaveBeenCalledOnceWith(1234);
  });

  it('should call updateTransportCompanyRelation and reloadRelations', () => {
    component.editMode = true;
    component.isUpdateRelationSelected = true;
    component.relationId = 1;

    component.form.setValue({
      businessOrganisation: { sboid: 'ch:1:sboid:100500' } as BusinessOrganisation,
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });

    expect(component.form.valid).toBeTrue();

    component.save();

    expect(component.form.untouched).toBeTrue();
    expect(component.editMode).toBeFalse();
    expect(component.isUpdateRelationSelected).toBeFalse();
    expect(
      transportCompanyRelationsServiceSpy.updateTransportCompanyRelation,
    ).toHaveBeenCalledOnceWith({
      id: 1,
      validFrom: moment('2020-05-05').toDate(),
      validTo: moment('2021-05-05').toDate(),
    });
    expect(
      transportCompanyRelationsServiceSpy.getTransportCompanyRelations,
    ).toHaveBeenCalledOnceWith(1234);
  });

  it('should call deleteTransportCompanyRelation and reload relations', () => {
    component.selectedTransportCompanyRelationIndex = 0;
    component.deleteRelation();
    expect(
      transportCompanyRelationsServiceSpy.deleteTransportCompanyRelation,
    ).toHaveBeenCalledOnceWith(1);
    expect(
      transportCompanyRelationsServiceSpy.getTransportCompanyRelations,
    ).toHaveBeenCalledOnceWith(1234);
  });
});

const transportCompanyRelationsServiceSpy = jasmine.createSpyObj(
  'TransportCompanyRelationsService',
  [
    'createTransportCompanyRelation',
    'getTransportCompanyRelations',
    'updateTransportCompanyRelation',
    'deleteTransportCompanyRelation',
  ],
);

function setupTestBed(data: (TransportCompany | TransportCompanyBoRelation[])[]) {
  transportCompanyRelationsServiceSpy.createTransportCompanyRelation.and.returnValue(of({}));
  transportCompanyRelationsServiceSpy.getTransportCompanyRelations.and.returnValue(of([]));
  transportCompanyRelationsServiceSpy.updateTransportCompanyRelation.and.returnValue(of({}));
  transportCompanyRelationsServiceSpy.deleteTransportCompanyRelation.and.returnValue(of({}));

  transportCompanyRelationsServiceSpy.createTransportCompanyRelation.calls.reset();
  transportCompanyRelationsServiceSpy.getTransportCompanyRelations.calls.reset();
  transportCompanyRelationsServiceSpy.updateTransportCompanyRelation.calls.reset();
  transportCompanyRelationsServiceSpy.deleteTransportCompanyRelation.calls.reset();

  TestBed.configureTestingModule({
    declarations: [
      TransportCompanyDetailComponent,
      RelationComponent,
      SearchSelectComponent,
      CommentComponent,
      MockDialogCloseComponent,
      MockAtlasButtonComponent,
      TextFieldComponent,
      AtlasLabelFieldComponent,
      AtlasFieldErrorComponent,
      DetailPageContainerComponent,
      DetailFooterComponent,
    ],
    imports: [AppTestingModule],
    providers: [
      {
        provide: ActivatedRoute,
        useValue: { snapshot: { data: { transportCompanyDetail: data } } },
      },
      {
        provide: AuthService,
        useValue: jasmine.createSpyObj<AuthService>(['hasPermissionsToCreate']),
      },
      {
        provide: TransportCompanyRelationsService,
        useValue: transportCompanyRelationsServiceSpy,
      },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
