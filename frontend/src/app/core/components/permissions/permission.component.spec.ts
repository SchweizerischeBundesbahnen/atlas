import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermissionComponent } from './permission.component';
import { TranslatePipe } from '@ngx-translate/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';
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
  const dialogServiceSpy = jasmine.createSpyObj('DialogService', [
    'confirmLeave',
  ]);
  dialogServiceSpy.confirmLeave.and.returnValue(of(true));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, PermissionComponent],
      providers: [
        TranslatePipe,
        translateServiceProvider,
        {
          provide: DialogService,
          useValue: dialogServiceSpy,
        },
        {
          provide: UserPermissionProviderService,
          useClass: MockUserPermissionProviderService,
        },
        provideHttpClient(),
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(PermissionComponent);
    component = fixture.componentInstance;
    userPermissionProviderService = TestBed.inject(
      UserPermissionProviderService
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should switch tabs without confirmation if form is not dirty', () => {
    expect(userPermissionProviderService.getCurrentForm()?.dirty).toBeFalse();
    const lidiTab = fixture.debugElement.query(By.css('.tab-LIDI'));

    const applicationChangedEmit = spyOn(component.applicationChanged, 'emit');
    lidiTab.nativeElement.click();
    expect(applicationChangedEmit).toHaveBeenCalledWith(ApplicationType.Lidi);
  });

  it('should switch tabs with confirmation if form is dirty', () => {
    userPermissionProviderService.getCurrentForm()?.markAsDirty();
    const lidiTab = fixture.debugElement.query(By.css('.tab-LIDI'));

    const applicationChangedEmit = spyOn(component.applicationChanged, 'emit');
    lidiTab.nativeElement.click();
    expect(dialogServiceSpy.confirmLeave).toHaveBeenCalled();
    expect(applicationChangedEmit).toHaveBeenCalledWith(ApplicationType.Lidi);
  });
});
