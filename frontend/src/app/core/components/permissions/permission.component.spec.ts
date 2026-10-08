import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { PermissionComponent } from './permission.component';
import { UserPermissionProviderService } from './application-permission/user-permission-provider-service';
import { By } from '@angular/platform-browser';
import { ApplicationRole, ApplicationType } from '../../../api';
import { DialogService } from '../dialog/dialog.service';
import { of } from 'rxjs';
import { MockUserPermissionProviderService, translateServiceProvider } from '../../../app.testing.mocks';

describe('PermissionComponent', () => {
  let component: PermissionComponent;
  let fixture: ComponentFixture<PermissionComponent>;

  let userPermissionProviderService: UserPermissionProviderService;
  let dialogServiceStub: Mocked<Pick<DialogService, 'confirmLeave'>>;

  beforeEach(() => {
    // Mocking
    dialogServiceStub = {
      confirmLeave: vi.fn().mockReturnValue(of(true)),
    };

    // Config
    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        {
          provide: DialogService,
          useValue: dialogServiceStub,
        },
        {
          provide: UserPermissionProviderService,
          useClass: MockUserPermissionProviderService,
        },
      ],
    });

    // Arrangement
    fixture = TestBed.createComponent(PermissionComponent);
    component = fixture.componentInstance;
    userPermissionProviderService = TestBed.inject(UserPermissionProviderService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should switch tabs without confirmation if form is not dirty', () => {
    expect(userPermissionProviderService.currentForm()?.().dirty()).toBe(false);
    const lidiTab = fixture.debugElement.query(By.css('.tab-LIDI'));

    const applicationChangedEmit = vi.spyOn(component.applicationChanged, 'emit').mockImplementation(() => {});
    lidiTab.nativeElement.click();
    expect(applicationChangedEmit).toHaveBeenCalledWith(ApplicationType.Lidi);
  });

  it('switches from BODI to a LIDI super user without reading that role as BODI', () => {
    const loadForm = userPermissionProviderService.loadForm.bind(userPermissionProviderService);
    vi.spyOn(userPermissionProviderService, 'loadForm').mockImplementation((application) => {
      loadForm(application);
      if (application === ApplicationType.Lidi) {
        userPermissionProviderService.currentForm()!.role().value.set(ApplicationRole.SuperUser);
      }
    });

    fixture.debugElement.query(By.css('.tab-BODI')).nativeElement.click();
    fixture.detectChanges();
    fixture.debugElement.query(By.css('.tab-LIDI')).nativeElement.click();
    fixture.detectChanges();

    expect(userPermissionProviderService.currentForm()?.().value()).toMatchObject({
      application: ApplicationType.Lidi,
      role: ApplicationRole.SuperUser,
    });
    expect(fixture.debugElement.query(By.css('.application-role'))).toBeTruthy();
  });

  it('should switch tabs with confirmation if form is dirty', () => {
    userPermissionProviderService.setEditable(true);
    userPermissionProviderService.currentForm()?.().markAsDirty();
    const lidiTab = fixture.debugElement.query(By.css('.tab-LIDI'));

    const applicationChangedEmit = vi.spyOn(component.applicationChanged, 'emit').mockImplementation(() => {});
    lidiTab.nativeElement.click();
    expect(dialogServiceStub.confirmLeave).toHaveBeenCalled();
    expect(applicationChangedEmit).toHaveBeenCalledWith(ApplicationType.Lidi);
  });

  it('keeps the current tab and edits when confirmation is declined', () => {
    userPermissionProviderService.setEditable(true);
    userPermissionProviderService.currentForm()?.().markAsDirty();
    dialogServiceStub.confirmLeave.mockReturnValue(of(false));
    const applicationChangedEmit = vi.spyOn(component.applicationChanged, 'emit');
    fixture.debugElement.query(By.css('.tab-LIDI')).nativeElement.click();

    expect(component.applicationTabs.selectedIndex).toBe(0);
    expect(userPermissionProviderService.currentForm()?.().dirty()).toBe(true);
    expect(applicationChangedEmit).not.toHaveBeenCalled();
  });
});
