import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationPermissionComponent } from './application-permission.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UserPermissionProviderService } from './user-permission-provider-service';
import { provideHttpClient } from '@angular/common/http';
import { ApplicationType } from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../form/application-permission-form-group';

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

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        ApplicationPermissionComponent,
      ],
      providers: [
        TranslatePipe,
        {
          provide: UserPermissionProviderService,
          useClass: MockUserPermissionProviderService,
        },
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
});
