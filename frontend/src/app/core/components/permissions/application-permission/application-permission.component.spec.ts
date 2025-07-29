import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { ApplicationPermissionComponent } from './application-permission.component';
import { TranslatePipe } from '@ngx-translate/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UserPermissionProviderService } from './user-permission-provider-service';
import { provideHttpClient } from '@angular/common/http';
import { ApplicationType, BusinessOrganisation } from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../form/application-permission-form-group';
import { of } from 'rxjs';
import { BusinessOrganisationInternalService } from '../../../../api/service/bodi/business-organisation-internal.service';
import SpyObj = jasmine.SpyObj;
import { translateServiceProvider } from '../../../../app.testing.mocks';

export class MockUserPermissionProviderService extends UserPermissionProviderService {
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;

  getCurrentForm(): FormGroup<ApplicationPermission> | undefined {
    return this.applicationPermissionFormGroup;
  }

  showAllSpecialPermissions(): boolean {
    return false;
  }

  loadFormGroup(): void {
    const formGroup = ApplicationPermissionFormGroupBuilder.buildFormGroup();
    formGroup.controls.application.setValue(ApplicationType.Ttfn);
    this.applicationPermissionFormGroup = formGroup;
  }
}

describe('ApplicationPermissionComponent', () => {
  let component: ApplicationPermissionComponent;
  let fixture: ComponentFixture<ApplicationPermissionComponent>;

  let businessOrganisationInternalServiceSpy: SpyObj<BusinessOrganisationInternalService>;

  beforeEach(() => {
    businessOrganisationInternalServiceSpy = jasmine.createSpyObj({
      getAllBusinessOrganisations: of({ objects: [] }),
    });

    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, ApplicationPermissionComponent],
      providers: [
        TranslatePipe,
        {
          provide: UserPermissionProviderService,
          useClass: MockUserPermissionProviderService,
        },
        {
          provide: BusinessOrganisationInternalService,
          useValue: businessOrganisationInternalServiceSpy,
        },
        translateServiceProvider,
        provideHttpClient(),
      ],
    });
    fixture = TestBed.createComponent(ApplicationPermissionComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('application', ApplicationType.Ttfn);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add  and remove businessOrganisation from table', fakeAsync(() => {
    expect(component.currentBusinessOrganisations.length).toBe(0);
    const businessOrganisation: BusinessOrganisation = {
      descriptionDe: 'de',
      descriptionFr: 'fr',
      descriptionIt: 'it',
      descriptionEn: 'en',
      abbreviationDe: 'de',
      abbreviationFr: 'fr',
      abbreviationIt: 'it',
      abbreviationEn: 'en',
      validFrom: new Date(),
      validTo: new Date(),
    };
    businessOrganisationInternalServiceSpy.getAllBusinessOrganisations.and.returnValue(
      of({ objects: [businessOrganisation] })
    );
    component.businessOrganisationForm.controls.businessOrganisation.setValue(
      businessOrganisation
    );
    // Add BusinessOrganisation
    component.addBusinessOrganisation();

    tick();
    expect(component.currentBusinessOrganisations.length).toBe(1);

    // Remove BusinessOrganisation via index
    component.selectedBusinessOrganisationIndex = 0;
    component.removeBusinessOrganisation();
    tick();
    expect(component.currentBusinessOrganisations.length).toBe(0);
  }));
});
