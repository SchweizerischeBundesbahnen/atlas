import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusinessOrganisationSelectComponent } from './business-organisation-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { of } from 'rxjs';
import { BusinessOrganisationInternalService } from '../../../api/service/bodi/business-organisation-internal.service';
import SpyObj = jasmine.SpyObj;
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('BusinessOrganisationSelectComponent', () => {
  let component: BusinessOrganisationSelectComponent;
  let fixture: ComponentFixture<BusinessOrganisationSelectComponent>;

  let businessOrganisationInternalService: SpyObj<BusinessOrganisationInternalService>;

  beforeEach(async () => {
    businessOrganisationInternalService = jasmine.createSpyObj({
      getAllBusinessOrganisations: of([]),
    });

    await TestBed.configureTestingModule({
      imports: [
        NgSelectModule,
        BusinessOrganisationSelectComponent,
        SearchSelectComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [
        TranslatePipe,
        {
          provide: BusinessOrganisationInternalService,
          useValue: businessOrganisationInternalService,
        },
        translateServiceProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BusinessOrganisationSelectComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      testControl: new FormControl(null),
    });
    component.controlName = 'testControl';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // To be able to find ch:1:sboid:1 we should sort by sboid instead of organisation number
  it('should search by businessOrganisation sorted by sboid', () => {
    component.searchBusinessOrganisation('ch:1:sboid:1');
    expect(
      businessOrganisationInternalService.getAllBusinessOrganisations
    ).toHaveBeenCalledWith(
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
