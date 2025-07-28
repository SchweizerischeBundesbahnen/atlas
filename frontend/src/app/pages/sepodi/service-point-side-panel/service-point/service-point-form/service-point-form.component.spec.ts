import { ServicePointFormComponent } from './service-point-form.component';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  Country,
  PermissionRestrictionType,
  ReadServicePointVersion,
  SpatialReference,
  SwissCanton,
} from '../../../../../api';
import { EventEmitter } from '@angular/core';
import { GeographyComponent } from '../../../geography/geography.component';
import { of } from 'rxjs';

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;

  const translationSortingServiceSpy = jasmine.createSpyObj(['sort'], {
    translateService: { onLangChange: jasmine.createSpyObj(['subscribe']) },
  });
  const geoDataServiceSpy = jasmine.createSpyObj(['getLocationInformation']);
  const authServiceSpy = jasmine.createSpyObj(['getApplicationUserPermission']);
  authServiceSpy.isAdmin = true;

  const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);

  beforeEach(() => {
    dialogServiceSpy.confirm.and.returnValue(of(true));
    component = new ServicePointFormComponent(
      translationSortingServiceSpy,
      dialogServiceSpy,
      geoDataServiceSpy,
      authServiceSpy
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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

    geoDataServiceSpy.getLocationInformation
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
    authServiceSpy.isAdmin = true;
    component.ngOnInit();

    expect(component.isNew).toBeTrue();
    expect(component.boSboidRestriction).toHaveSize(0);
  });

  it('should show only allowed bos on new for writer', () => {
    authServiceSpy.isAdmin = false;
    authServiceSpy.getApplicationUserPermission.and.returnValue({
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.BusinessOrganisation,
          valueAsString: 'ch:1:sboid:213',
        },
      ],
    });

    component.ngOnInit();

    expect(component.isNew).toBeTrue();
    expect(component.boSboidRestriction).toHaveSize(1);
  });
});
