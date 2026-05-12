import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusinessOrganisationSelectComponent } from './business-organisation-select.component';
import { FormControl, FormGroup } from '@angular/forms';
import { of } from 'rxjs';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { beforeEach, describe, expect, it } from 'vitest';
import { mock, mockClear } from 'vitest-mock-extended';
import { inputBinding, signal } from '@angular/core';

describe('BusinessOrganisationSelectComponent', () => {
  let component: BusinessOrganisationSelectComponent;
  let fixture: ComponentFixture<BusinessOrganisationSelectComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let controlNameInput: ReturnType<typeof signal<string>>;

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

    const formGroupInputName: keyof BusinessOrganisationSelectComponent = 'formGroup';
    const controlNameInputName: keyof BusinessOrganisationSelectComponent = 'controlName';
    formGroupInput = signal(
      new FormGroup({
        testControl: new FormControl(null),
      })
    );
    controlNameInput = signal('testControl');
    fixture = TestBed.createComponent(BusinessOrganisationSelectComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
      ],
    });
    component = fixture.componentInstance;

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
