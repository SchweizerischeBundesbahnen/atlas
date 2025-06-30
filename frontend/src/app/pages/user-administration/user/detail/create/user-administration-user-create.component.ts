import { Component } from '@angular/core';
import { BusinessOrganisationsService, User } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../../pages';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { FormControl, FormGroup } from '@angular/forms';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { MatLabel } from '@angular/material/form-field';
import { UserSelectComponent } from '../../user-select/user-select.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';

@Component({
  selector: 'app-user-administration-create',
  templateUrl: './user-administration-user-create.component.html',
  viewProviders: [BusinessOrganisationsService],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    MatLabel,
    UserSelectComponent,
    DetailFooterComponent,
    TranslatePipe,
  ],
})
export class UserAdministrationUserCreateComponent {
  selectedUser?: User;
  userHasAlreadyPermissions = false;
  selectedUserHasNoUserId = false;
  saveEnabled = true;
  readonly userSearchForm: FormGroup = new FormGroup({
    userSearch: new FormControl<string | null>(null),
  });

  constructor(
    private readonly userService: UserAdministrationService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dialogService: DialogService
  ) {}

  selectUser(user: User | undefined): void {
    this.selectedUserHasNoUserId = false;
    if (!user?.sbbUserId) {
      this.userHasAlreadyPermissions = false;
      this.selectedUser = undefined;
      if (user) {
        this.selectedUserHasNoUserId = true;
      }
      return;
    }
    this.userService.getUser(user.sbbUserId).subscribe((user) => {
      this.selectedUser = user;
      this.userHasAlreadyPermissions = true;
    });
  }

  createUser(): void {
    this.saveEnabled = false;
    this.userService
      .createUserPermission({ sbbUserId: this.selectedUser!.userId! })
      .subscribe({
        next: () => {
          this.router
            .navigate(
              [Pages.USER_ADMINISTRATION.path, this.selectedUser!.userId!],
              {
                relativeTo: this.route,
              }
            )
            .then(() =>
              this.notificationService.success(
                'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
              )
            );
        },
        error: () => (this.saveEnabled = true),
      });
  }

  cancelCreation(showDialog = true): void {
    if (!showDialog) {
      this.navigateBack();
      return;
    }
    this.dialogService.confirmLeave().subscribe((result) => {
      if (result) {
        this.navigateBack();
      }
    });
  }

  navigateBack() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }
}
