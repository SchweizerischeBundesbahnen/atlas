import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { PermissionComponent } from './permission.component';
import { UserPermissionProviderService } from './application-permission/user-permission-provider-service';
import { MockUserPermissionProviderService } from './application-permission/application-permission.component.spec';
import { By } from '@angular/platform-browser';
import { ApplicationType } from '../../../api';
import { DialogService } from '../dialog/dialog.service';
import { of } from 'rxjs';
import { translateServiceProvider } from '../../../app.testing.mocks';

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
    expect(userPermissionProviderService.getCurrentForm()?.dirty).toBe(false);
    const lidiTab = fixture.debugElement.query(By.css('.tab-LIDI'));

    const applicationChangedEmit = vi.spyOn(component.applicationChanged, 'emit').mockImplementation(() => {});
    lidiTab.nativeElement.click();
    expect(applicationChangedEmit).toHaveBeenCalledWith(ApplicationType.Lidi);
  });

  it('should switch tabs with confirmation if form is dirty', () => {
    userPermissionProviderService.getCurrentForm()?.markAsDirty();
    const lidiTab = fixture.debugElement.query(By.css('.tab-LIDI'));

    const applicationChangedEmit = vi.spyOn(component.applicationChanged, 'emit').mockImplementation(() => {});
    lidiTab.nativeElement.click();
    expect(dialogServiceStub.confirmLeave).toHaveBeenCalled();
    expect(applicationChangedEmit).toHaveBeenCalledWith(ApplicationType.Lidi);
  });
});
