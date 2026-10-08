import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { User } from '../../../../../api';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { BackButtonDirective } from '../../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe } from '@ngx-translate/core';
import { PermissionComponent } from '../../../../../core/components/permissions/permission.component';
import { UserPermissionGivenUserService } from './user-permission-given-user.service';
import { ApplicationPermissionFormBuilder } from '../../../../../core/components/permissions/form/application-permission-form-group';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';
import { ConvertUserPermissionToRecordHelper } from '../../../../../core/components/permissions/helper/convert-user-permission-to-record-helper';
import { UserDetailInfoComponent } from '../../../../../core/components/user-edit-info/user-detail-info.component';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import {
  ManualMailOverrideDialogComponent,
  ManualMailOverrideDialogData,
} from './manual-mail-override-dialog/manual-mail-override-dialog.component';

@Component({
  selector: 'atlas-user-administration-user-edit',
  templateUrl: './user-administration-user-edit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
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
  private readonly userPermissionGivenUserService = inject(UserPermissionGivenUserService);
  private readonly userAdministrationService = inject(UserAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly dialogService = inject(DialogService);
  private readonly permissionService = inject(PermissionService);
  readonly user = input.required<User>();
  private readonly updatedUser = signal<User | undefined>(undefined);
  readonly displayUser = computed(() => this.updatedUser() ?? this.user());
  readonly userRecord = computed(() =>
    ConvertUserPermissionToRecordHelper.convertUserPermissionToRecord(Array.from(this.displayUser().permissions))
  );
  readonly editMode = signal(false);

  ngOnInit() {
    this.userPermissionGivenUserService.user = this.user();
  }

  get isAdmin(): boolean {
    return this.permissionService.isAdmin;
  }

  editManualMail(): void {
    this.dialogService
      .openDialogDataWithCustomResult<ManualMailOverrideDialogData, User>(
        {
          title: 'USER_ADMIN.MANUAL_MAIL_DIALOG.TITLE',
          message: 'USER_ADMIN.MANUAL_MAIL_DIALOG.HINT',
          user: this.userPermissionGivenUserService.user,
        },
        ManualMailOverrideDialogComponent
      )
      .subscribe((updatedUser) => {
        if (updatedUser) {
          this.userPermissionGivenUserService.user = updatedUser;
          this.updatedUser.set(updatedUser);
        }
      });
  }

  saveUser(): void {
    const form = this.formGroup;
    const userPermission = ApplicationPermissionFormBuilder.formToModel(form);
    this.userPermissionGivenUserService.setEditable(false);
    this.userAdministrationService
      .updateUserPermission(this.user().sbbUserId, userPermission.application, userPermission)
      .subscribe({
        next: (user) => {
          this.userPermissionGivenUserService.user = user;
          this.updatedUser.set(user);
          this.editMode.set(false);
          this.userPermissionGivenUserService.loadForm(userPermission.application);
          this.notificationService.success('USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS');
        },
        error: (error) => {
          this.userPermissionGivenUserService.setEditable(true);
          this.notificationService.error(error);
        },
      });
  }

  toggleEdit() {
    if (!this.userPermissionGivenUserService.editable()) {
      this.userPermissionGivenUserService.setEditable(true);
      this.editMode.set(true);
    } else if (this.formGroup().dirty()) {
      this.dialogService.confirmLeave().subscribe((result) => {
        if (result) {
          this.editMode.set(false);
          this.userPermissionGivenUserService.loadForm(this.formGroup().value().application);
        }
      });
    } else {
      this.editMode.set(false);
      this.userPermissionGivenUserService.setEditable(false);
    }
  }

  get formGroup() {
    const form = this.userPermissionGivenUserService.currentForm();
    if (!form) {
      throw new Error('Permission form has not been loaded');
    }
    return form;
  }
}
