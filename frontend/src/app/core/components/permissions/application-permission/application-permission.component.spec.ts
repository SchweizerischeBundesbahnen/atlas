import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ApplicationPermissionComponent } from './application-permission.component';
import { UserPermissionProviderService } from './user-permission-provider-service';
import { ApplicationRole, ApplicationType, BusinessOrganisation, PermissionRestrictionType } from '../../../../api';
import { of, throwError } from 'rxjs';
import { BusinessOrganisationService } from '../../../../api/service/bodi/business-organisation.service';
import { MockUserPermissionProviderService, translateServiceProvider } from '../../../../app.testing.mocks';

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

  it('does not render a BODI tab against a LIDI super user form during a tab switch', () => {
    const provider = TestBed.inject(UserPermissionProviderService);
    fixture.componentRef.setInput('application', ApplicationType.Bodi);
    provider.loadForm(ApplicationType.Bodi);
    fixture.detectChanges();

    provider.loadForm(ApplicationType.Lidi);
    provider.currentForm()?.role().value.set(ApplicationRole.SuperUser);
    fixture.detectChanges();

    expect(component.form()).toBeUndefined();
    expect((fixture.nativeElement as HTMLElement).querySelector('.application-role')).toBeNull();

    provider.loadForm(ApplicationType.Bodi);
    fixture.detectChanges();
    expect(component.currentRole()).toBe(ApplicationRole.Reader);
    expect((fixture.nativeElement as HTMLElement).querySelector('.application-role')).toBeTruthy();
  });

  it('should add  and remove businessOrganisation from table', async () => {
    TestBed.inject(UserPermissionProviderService).setEditable(true);
    component.form()?.role().value.set(ApplicationRole.Writer);
    expect(component.currentBusinessOrganisations().length).toBe(0);
    const businessOrganisation: BusinessOrganisation = {
      sboid: 'ch:1:sboid:10000',
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
    component.businessOrganisationForm.businessOrganisation().value.set(businessOrganisation);

    // Add BusinessOrganisation
    component.addBusinessOrganisation();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(component.currentBusinessOrganisations().length).toBe(1);
    expect(component.form()?.permissions.sboidsRestrictions().value()).toEqual(['ch:1:sboid:10000']);
    expect(component.form()?.().dirty()).toBe(true);

    // Remove BusinessOrganisation via index
    component.selectedBusinessOrganisationIndex.set(0);
    component.removeBusinessOrganisation();
    expect(component.currentBusinessOrganisations().length).toBe(0);
    expect(component.form()?.permissions.sboidsRestrictions().value()).toEqual([]);
  });

  it('restricts role-specific permissions and keeps nova and info plus mutually exclusive', () => {
    const provider = TestBed.inject(UserPermissionProviderService);
    fixture.componentRef.setInput('application', ApplicationType.Sepodi);
    fixture.detectChanges();
    provider.loadForm(ApplicationType.Sepodi);
    component.form()?.role().value.set(ApplicationRole.Writer);
    expect(component.currentRoleConfig().permissions.restrictions).toContain(PermissionRestrictionType.Country);
    component.form()?.permissions.infoPlusTerminationVote().value.set(true);
    component.onNovaToggle(true);
    expect(component.form()?.permissions.infoPlusTerminationVote().value()).toBe(false);
    component.form()?.permissions.novaTerminationVote().value.set(true);
    component.onInfoPlusToggle(true);
    expect(component.form()?.permissions.novaTerminationVote().value()).toBe(false);
  });

  it('shows a lookup error without losing restrictions when an organisation cannot be fetched', async () => {
    businessOrganisationServiceStub.getAllBusinessOrganisations.mockReturnValue(
      throwError(() => new Error('Lookup failed'))
    );
    component.form()?.role().value.set(ApplicationRole.Writer);
    component.form()?.permissions.sboidsRestrictions().value.set(['ch:1:sboid:10000']);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.lookupError()).toBe(true);
    expect(component.form()?.permissions.sboidsRestrictions().value()).toEqual(['ch:1:sboid:10000']);
    expect((fixture.nativeElement as HTMLElement).querySelector('[role=alert]')).toBeTruthy();
  });
});
