import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusinessOrganisationSelectComponent } from './business-organisation-select.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { MaterialModule } from '../../module/material.module';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import {BusinessOrganisationsService} from "../../../api";
import {of} from "rxjs";

const businessOrganisationsService = jasmine.createSpyObj('businessOrganisationsService', ['getAllBusinessOrganisations']);
businessOrganisationsService.getAllBusinessOrganisations.and.returnValue(of([]));

describe('BusinessOrganisationSelectComponent', () => {
  let component: BusinessOrganisationSelectComponent;
  let fixture: ComponentFixture<BusinessOrganisationSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        BusinessOrganisationSelectComponent,
        SearchSelectComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        NgSelectModule,
        MaterialModule,
        HttpClientTestingModule,
      ],
      providers: [
        TranslatePipe,
        {
          provide: BusinessOrganisationsService,
          useValue: businessOrganisationsService,
        },
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
      businessOrganisationsService.getAllBusinessOrganisations
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
