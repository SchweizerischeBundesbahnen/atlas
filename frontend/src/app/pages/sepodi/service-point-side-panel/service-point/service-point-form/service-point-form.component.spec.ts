import { ServicePointFormComponent } from './service-point-form.component';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  Country,
  GeoDataService,
  Permission,
  PermissionRestrictionType,
  ReadServicePointVersion,
  SpatialReference,
  SwissCanton,
} from '../../../../../api';
import { EventEmitter } from '@angular/core';
import { GeographyComponent } from '../../../geography/geography.component';
import { of } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DateRangeComponent } from '../../../../../core/form-components/date-range/date-range.component';
import { BusinessOrganisationSelectComponent } from '../../../../../core/form-components/bo-select/business-organisation-select.component';
import { MatLabel } from '@angular/material/form-field';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { SelectComponent } from '../../../../../core/form-components/select/select.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { MeansOfTransportPickerComponent } from '../../../means-of-transport-picker/means-of-transport-picker.component';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { KilometerMasterSearchComponent } from '../search/kilometer-master-search.component';
import { DisplayCantonPipe } from '../../../../../core/cantons/display-canton.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { TranslationSortingService } from '../../../../../core/translation/translation-sorting.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;
  let fixture: ComponentFixture<ServicePointFormComponent>;

  let translationSortingServiceSpy: SpyObj<TranslationSortingService>;
  let dialogServiceSpy: SpyObj<DialogService>;
  let geoDataServiceSpy: SpyObj<GeoDataService>;

  let isAdmin = true;
  let permission = {} as Permission;
  const permissionServiceMock: Partial<PermissionService> = {
    get isAdmin() {
      return isAdmin;
    },
    getApplicationUserPermission: () => permission,
  };

  beforeEach(async () => {
    translationSortingServiceSpy = jasmine.createSpyObj(['sort'], {
      translateService: { onLangChange: jasmine.createSpyObj(['subscribe']) },
    });
    dialogServiceSpy = jasmine.createSpyObj('DialogService', {
      confirm: of(true),
    });
    geoDataServiceSpy = jasmine.createSpyObj(['getLocationInformation']);

    await TestBed.configureTestingModule({
      imports: [
        TextFieldComponent,
        ReactiveFormsModule,
        DateRangeComponent,
        BusinessOrganisationSelectComponent,
        MatLabel,
        MatRadioGroup,
        MatRadioButton,
        AtlasFieldErrorComponent,
        SelectComponent,
        MatCheckbox,
        MeansOfTransportPickerComponent,
        NgTemplateOutlet,
        KilometerMasterSearchComponent,
        DisplayCantonPipe,
        AsyncPipe,
        TranslatePipe,
      ],
      providers: [
        {
          provide: TranslationSortingService,
          useValue: translationSortingServiceSpy,
        },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: GeoDataService, useValue: geoDataServiceSpy },
        { provide: PermissionService, useValue: permissionServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointFormComponent);
    component = fixture.componentInstance;
  });

  it('should update locationInformation when coordinates changed', (done) => {
    component['_currentVersion'] = { id: 5 } as ReadServicePointVersion;
    component.geographyComponent = {
      coordinatesChanged: new EventEmitter<CoordinatePair>(),
    } as GeographyComponent;

    const coordinatePair = {
      spatialReference: SpatialReference.Lv95,
      north: 5,
      east: 6,
    };

    (geoDataServiceSpy.getLocationInformation as Spy)
      .withArgs(coordinatePair)
      .and.returnValue(
        of({
          country: Country.Cuba,
          swissCanton: SwissCanton.Aargau,
          swissMunicipalityName: 'Gemeinde',
          swissLocalityName: 'Ort',
        })
      );

    component.ngOnInit();

    component.geographyComponent.coordinatesChanged.emit(coordinatePair);

    component.locationInformation$?.subscribe((locationInformation) => {
      expect(locationInformation.canton).toEqual(SwissCanton.Aargau);
      expect(locationInformation.isoCountryCode).toEqual('CU');
      expect(locationInformation.municipalityName).toEqual('Gemeinde');
      expect(locationInformation.localityName).toEqual('Ort');
      done();
    });
  });

  it('should show all bos on edit', () => {
    component['_currentVersion'] = { id: 5 } as ReadServicePointVersion;
    component.ngOnInit();

    expect(component.isNew).toBeFalse();
    expect(component.boSboidRestriction).toHaveSize(0);
  });

  it('should show all bos new for admin', () => {
    isAdmin = true;
    component.ngOnInit();

    expect(component.isNew).toBeTrue();
    expect(component.boSboidRestriction).toHaveSize(0);
  });

  it('should show only allowed bos on new for writer', () => {
    isAdmin = false;
    permission = {
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.BusinessOrganisation,
          valueAsString: 'ch:1:sboid:213',
        },
      ],
    };

    component.ngOnInit();

    expect(component.isNew).toBeTrue();
    expect(component.boSboidRestriction).toHaveSize(1);
  });
});
