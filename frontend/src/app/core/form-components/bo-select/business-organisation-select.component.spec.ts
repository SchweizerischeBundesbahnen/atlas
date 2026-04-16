import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusinessOrganisationSelectComponent } from './business-organisation-select.component';
import { FormControl, FormGroup } from '@angular/forms';
import { of } from 'rxjs';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { beforeEach, describe, expect, it } from 'vitest';
import { mock, mockClear } from 'vitest-mock-extended';

describe('BusinessOrganisationSelectComponent', () => {
  let component: BusinessOrganisationSelectComponent;
  let fixture: ComponentFixture<BusinessOrganisationSelectComponent>;

  const businessOrganisationServiceSpy = mock<BusinessOrganisationService>();
  businessOrganisationServiceSpy.getAllBusinessOrganisations.mockReturnValue(of({ objects: [] }));

  beforeEach(() => {
    mockClear(businessOrganisationServiceSpy);

    TestBed.configureTestingModule({
      providers: [
        {
          provide: BusinessOrganisationService,
          useValue: businessOrganisationServiceSpy,
        },
        translateServiceProvider,
      ],
    });

    fixture = TestBed.createComponent(BusinessOrganisationSelectComponent);
    component = fixture.componentInstance;

    component.formGroup = new FormGroup({
      testControl: new FormControl(null),
    });
    component.controlName = 'testControl';

    fixture.detectChanges();
  });

  // To be able to find ch:1:sboid:1 we should sort by sboid instead of organisation number
  it('should search by businessOrganisation sorted by sboid', () => {
    component.searchBusinessOrganisation('ch:1:sboid:1');
    expect(businessOrganisationServiceSpy.getAllBusinessOrganisations).toHaveBeenCalledExactlyOnceWith(
      ['ch:1:sboid:1'],
      [],
      undefined,
      undefined,
      undefined,
      undefined,
      ['sboid,ASC']
    );
  });
});
