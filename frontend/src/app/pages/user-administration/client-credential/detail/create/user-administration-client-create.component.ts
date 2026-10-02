import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../../pages';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { form } from '@angular/forms/signals';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { AtlasTextFieldComponent } from '@atlas/form';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';
import { ClientCredentialCreate } from '../../../../../api/model/clientCredentialCreate';
import { FormValidators } from '../../../../../core/validation/form-validators.service';

type ClientCredentialCreateFormModel = Required<ClientCredentialCreate>;

@Component({
  selector: 'atlas-client-credential-administration-create',
  templateUrl: './user-administration-client-create.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    AtlasTextFieldComponent,
    DetailFooterComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class UserAdministrationClientCreateComponent {
  private readonly clientCredentialAdministrationService = inject(ClientCredentialAdministrationService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly dialogService = inject(DialogService);
  private readonly formValidators = inject(FormValidators);

  readonly saveEnabled = signal(true);

  readonly formModel = signal<ClientCredentialCreateFormModel>({
    clientCredentialId: '',
    alias: '',
    comment: '',
  });
  readonly form = form(this.formModel, (path) => {
    this.formValidators.required(path.clientCredentialId);
    this.formValidators.iso88591(path.clientCredentialId);
    this.formValidators.maxLength(path.clientCredentialId, 50);
    this.formValidators.blankOrEmptySpaceSurrounding(path.clientCredentialId);

    this.formValidators.required(path.alias);
    this.formValidators.iso88591(path.alias);
    this.formValidators.maxLength(path.alias, 100);
    this.formValidators.blankOrEmptySpaceSurrounding(path.alias);

    this.formValidators.iso88591(path.comment);
    this.formValidators.maxLength(path.comment, 100);
    this.formValidators.blankOrEmptySpaceSurrounding(path.comment);
  });

  create(): void {
    this.form().markAsTouched();
    if (this.form().valid() && this.saveEnabled()) {
      this.saveEnabled.set(false);
      const permission = { ...this.formModel() };
      this.clientCredentialAdministrationService.createClientCredential(permission).subscribe({
        next: () => {
          this.router
            .navigate([Pages.USER_ADMINISTRATION.path, Pages.CLIENTS.path, permission.clientCredentialId])
            .then(() => this.notificationService.success('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'));
        },
        error: () => this.saveEnabled.set(true),
      });
    }
  }

  cancelCreation(): void {
    if (this.form().touched()) {
      this.dialogService.confirmLeave().subscribe((result) => {
        if (result) {
          this.back();
        }
      });
    } else {
      this.back();
    }
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }
}
