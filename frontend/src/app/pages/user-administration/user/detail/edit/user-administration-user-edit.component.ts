import { Component, inject, input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { CreationEditionRecord } from '../../../../../core/components/base-detail/user-edit-info/creation-edition-record';
import { User } from '../../../../../api';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { BackButtonDirective } from '../../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe } from '@ngx-translate/core';
import { PermissionComponent } from '../../../../../core/components/permissions/permission.component';
import { UserPermissionGivenUserService } from './user-permission-given-user.service';
import { ApplicationPermissionFormGroupBuilder } from '../../../../../core/components/permissions/form/application-permission-form-group';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';
import { ConvertUserPermissionToRecordHelper } from '../../../../../core/components/permissions/helper/convert-user-permission-to-record-helper';
import { UserDetailInfoComponent } from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';

@Component({
  selector: 'app-user-administration-user-edit',
  templateUrl: './user-administration-user-edit.component.html',
  styleUrls: ['./user-administration-user-edit.component.scss'],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    DetailFooterComponent,
    BackButtonDirective,
    TranslatePipe,
    PermissionComponent,
    UserDetailInfoComponent,
  ],
  providers: [TranslatePipe],
})
export class UserAdministrationUserEditComponent implements OnInit {
  user = input.required<User>();

  userRecord?: CreationEditionRecord;
  editMode = false;

  userPermissionGivenUserService = inject(UserPermissionGivenUserService);
  userAdministrationService = inject(UserAdministrationService);
  notificationService = inject(NotificationService);
  dialogService = inject(DialogService);

  ngOnInit() {
    this.userPermissionGivenUserService.user = this.user();
    this.convertUserPermissionToRecord();
  }

  saveUser(): void {
    const userPermission = ApplicationPermissionFormGroupBuilder.formToModel(
      this.formGroup
    );
    this.formGroup.disable();
    this.userAdministrationService
      .updateUserPermission(
        this.user().sbbUserId,
        userPermission.application,
        userPermission
      )
      .subscribe({
        next: (user) => {
          this.userPermissionGivenUserService.user = user;
          this.editMode = false;
          this.userPermissionGivenUserService.loadFormGroup(
            this.userPermissionGivenUserService.getCurrentForm()!.controls
              .application.value!
          );
          this.notificationService.success(
            'USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS'
          );
          this.convertUserPermissionToRecord();
        },
        error: () => this.formGroup.enable(),
      });
  }

  toggleEdit() {
    if (this.formGroup.disabled) {
      this.formGroup.enable();
      this.editMode = true;
    } else if (this.formGroup.dirty) {
      this.dialogService.confirmLeave().subscribe((result) => {
        if (result) {
          this.editMode = false;
          this.userPermissionGivenUserService.loadFormGroup(
            this.userPermissionGivenUserService.getCurrentForm()!.controls
              .application.value!
          );
        }
      });
    } else {
      this.editMode = false;
      this.formGroup.disable();
    }
  }

  get formGroup() {
    return this.userPermissionGivenUserService.applicationPermissionFormGroup!;
  }

  private convertUserPermissionToRecord(): void {
    const permissionsFromUserModelAsArray = Array.from(
      this.userPermissionGivenUserService.user.permissions
    );

    this.userRecord =
      ConvertUserPermissionToRecordHelper.convertUserPermissionToRecord(
        permissionsFromUserModelAsArray
      );
  }
}
