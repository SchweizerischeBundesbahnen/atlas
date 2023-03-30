import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { BusinessOrganisationComponent } from './business-organisation.component';
import { BusinessOrganisationsService, ContainerBusinessOrganisation } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';

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

  // With Spy
  const businessOrganisationsService = jasmine.createSpyObj('businessOrganisationsService', [
    'getAllBusinessOrganisations',
  ]);
  businessOrganisationsService.getAllBusinessOrganisations.and.returnValue(
    of(businessOrganisation)
  );

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BusinessOrganisationComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: BusinessOrganisationsService, useValue: businessOrganisationsService },
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
});
