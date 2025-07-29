import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { UserAdministrationClientCreateComponent } from './user-administration-client-create.component';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import SpyObj = jasmine.SpyObj;

describe('UserAdministrationClientCreateComponent', () => {
  let component: UserAdministrationClientCreateComponent;
  let fixture: ComponentFixture<UserAdministrationClientCreateComponent>;

  let clientCredentialAdministrationServiceSpy: SpyObj<ClientCredentialAdministrationService>;
  let notificationServiceSpy: SpyObj<NotificationService>;

  beforeEach(async () => {
    clientCredentialAdministrationServiceSpy = jasmine.createSpyObj(
      'ClientCredentialAdministrationService',
      ['createClientCredential']
    );
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
    ]);

    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        UserAdministrationClientCreateComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
      ],
      providers: [
        provideHttpClient(),
        translateServiceProvider,
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        TranslatePipe,
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: undefined },
        },
        {
          provide: MatDialogRef,
          useValue: {
            close: () => {
              // mock implementation
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create client', fakeAsync(() => {
    const router = TestBed.inject(Router);
    component.form.controls.clientCredentialId.setValue('client-id');
    component.form.controls.alias.setValue('alias');

    clientCredentialAdministrationServiceSpy.createClientCredential.and.returnValue(
      of({
        clientCredentialId: 'client-id',
      })
    );
    spyOn(router, 'navigate').and.resolveTo(true);

    component.create();
    expect(
      clientCredentialAdministrationServiceSpy.createClientCredential
    ).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
    tick();
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
    );
  }));
});
