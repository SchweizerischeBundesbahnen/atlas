import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { User } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../../pages';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { form } from '@angular/forms/signals';
import { Subscription } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { MatLabel } from '@angular/material/form-field';
import { UserSelectComponent } from '../../user-select/user-select.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';

@Component({
  selector: 'atlas-user-administration-create',
  templateUrl: './user-administration-user-create.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    MatLabel,
    UserSelectComponent,
    DetailFooterComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class UserAdministrationUserCreateComponent {
  private readonly userService = inject(UserAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly dialogService = inject(DialogService);
  private readonly destroyRef = inject(DestroyRef);
  private selectedUserSubscription?: Subscription;

  readonly userSearchModel = signal<{ userSearch: User | null }>({ userSearch: null });
  readonly userSearchForm = form(this.userSearchModel);

  readonly selectedUser = signal<User | undefined>(undefined);
  readonly userHasAlreadyPermissions = signal(false);
  readonly selectedUserHasNoUserId = signal(false);
  readonly saveEnabled = signal(true);

  selectUser(user: User | undefined): void {
    this.selectedUserSubscription?.unsubscribe();
    this.selectedUser.set(undefined);
    this.userHasAlreadyPermissions.set(false);
    this.selectedUserHasNoUserId.set(false);
    if (!user?.sbbUserId) {
      if (user) {
        this.selectedUserHasNoUserId.set(true);
      }
      return;
    }
    this.selectedUserSubscription = this.userService
      .getUser(user.sbbUserId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((selectedUser) => {
        this.selectedUser.set(selectedUser);
        this.selectedUserHasNoUserId.set(!selectedUser.userId);
        this.userHasAlreadyPermissions.set(selectedUser.permissions.size > 0);
      });
  }

  createUser(): void {
    const selectedUser = this.selectedUser();
    if (!selectedUser?.userId) {
      throw new Error('A user ID is required to create a permission');
    }
    if (this.userHasAlreadyPermissions() || !this.saveEnabled()) {
      return;
    }
    this.saveEnabled.set(false);
    this.userService.createUserPermission({ sbbUserId: selectedUser.userId }).subscribe({
      next: () => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path, Pages.USERS.path, selectedUser.userId])
          .then(() => this.notificationService.success('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'));
      },
      error: () => this.saveEnabled.set(true),
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
