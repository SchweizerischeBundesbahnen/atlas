import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ApplicationPermissionComponent } from './application-permission.component';
import { UserPermissionProviderService } from './user-permission-provider-service';
import { ApplicationType, BusinessOrganisation } from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../form/application-permission-form-group';
import { of } from 'rxjs';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';
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

  let businessOrganisationServiceStub: Mocked<Pick<BusinessOrganisationService, 'getAllBusinessOrganisations'>>;

  beforeEach(() => {
    // Mocking
    businessOrganisationServiceStub = {
      getAllBusinessOrganisations: vi.fn().mockReturnValue(of({ objects: [] })),
    };

    // Config
    TestBed.configureTestingModule({
      providers: [
        {
          provide: UserPermissionProviderService,
          useClass: MockUserPermissionProviderService,
        },
        {
          provide: BusinessOrganisationService,
          useValue: businessOrganisationServiceStub,
        },
        translateServiceProvider,
      ],
    });

    // Arrangement
    fixture = TestBed.createComponent(ApplicationPermissionComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('application', ApplicationType.Ttfn);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add  and remove businessOrganisation from table', async () => {
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
    businessOrganisationServiceStub.getAllBusinessOrganisations.mockReturnValue(
      of({ objects: [businessOrganisation] })
    );
    component.businessOrganisationForm.controls.businessOrganisation.setValue(businessOrganisation);

    // Add BusinessOrganisation
    component.addBusinessOrganisation();
    await fixture.whenStable();
    expect(component.currentBusinessOrganisations.length).toBe(1);

    // Remove BusinessOrganisation via index
    component.selectedBusinessOrganisationIndex = 0;
    component.removeBusinessOrganisation();
    expect(component.currentBusinessOrganisations.length).toBe(0);
  });

  it('should set transportCompanyDossierAnswer to true', () => {
    component.onTransportCompanyDossierToggle(true);

    const control = component.form.controls.permissions.controls.transportCompanyDossierAnswer;
    expect(control!.value).toBe(true);
  });

  it('should set transportCompanyDossierAnswer to false', () => {
    component.onTransportCompanyDossierToggle(false);

    const control = component.form.controls.permissions.controls.transportCompanyDossierAnswer;
    expect(control!.value).toBe(false);
  });
});
