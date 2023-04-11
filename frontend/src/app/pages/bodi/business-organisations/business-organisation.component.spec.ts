import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { BusinessOrganisationComponent } from './business-organisation.component';
import { BusinessOrganisationsService, ContainerBusinessOrganisation } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import Spy = jasmine.Spy;

const businessOrganisation: ContainerBusinessOrganisation = {
  objects: [
    {
      sboid: 'sboid',
      descriptionDe: 'asdf',
      descriptionFr: 'asdf',
      descriptionIt: 'asdf',
      descriptionEn: 'asdf',
      abbreviationDe: 'asdf',
      abbreviationFr: 'asdf',
      abbreviationIt: 'asdf',
      abbreviationEn: 'asdf',
      status: 'VALIDATED',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
    },
  ],
  totalCount: 1,
};

describe('BusinessOrganisationComponent', () => {
  let component: BusinessOrganisationComponent;
  let fixture: ComponentFixture<BusinessOrganisationComponent>;

  let businessOrganisationsServiceSpy: jasmine.SpyObj<BusinessOrganisationsService>;

  beforeEach(() => {
    businessOrganisationsServiceSpy = jasmine.createSpyObj<BusinessOrganisationsService>(
      'BusinessOrganisationsServiceSpy',
      ['getAllBusinessOrganisations']
    );

    (
      businessOrganisationsServiceSpy.getAllBusinessOrganisations as Spy<
        () => Observable<ContainerBusinessOrganisation>
      >
    ).and.returnValue(of(businessOrganisation));

    TestBed.configureTestingModule({
      declarations: [BusinessOrganisationComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: BusinessOrganisationsService, useValue: businessOrganisationsServiceSpy },
        TranslatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BusinessOrganisationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(businessOrganisationsServiceSpy.getAllBusinessOrganisations).toHaveBeenCalledOnceWith(
      [],
      undefined,
      undefined,
      DEFAULT_STATUS_SELECTION,
      0,
      10,
      ['descriptionDe,asc']
    );

    expect(component.totalCount$).toEqual(1);
    expect(component.businessOrganisations.length).toEqual(1);
    expect(component.businessOrganisations[0].sboid).toEqual('sboid');
  });
});
