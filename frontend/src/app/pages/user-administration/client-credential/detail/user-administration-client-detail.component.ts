import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { ClientCredential } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationClientEditComponent } from './edit/user-administration-client-edit.component';
import { UserAdministrationClientCreateComponent } from './create/user-administration-client-create.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { UserPermissionGivenClientService } from './edit/user-permission-given-client.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';

const emptyClientCredential: ClientCredential = {};

@Component({
  selector: 'atlas-client-credential-administration',
  templateUrl: './user-administration-client-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [UserAdministrationClientEditComponent, UserAdministrationClientCreateComponent],
})
export class UserAdministrationClientDetailComponent implements DetailFormComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly userPermissionGivenClientService = inject(UserPermissionGivenClientService);

  readonly clientCredential: Signal<ClientCredential> = toSignal(
    this.activatedRoute.data.pipe(map((data) => data['clientCredential'] as ClientCredential)),
    { initialValue: emptyClientCredential }
  );

  readonly dirty = computed(() => {
    const form = this.userPermissionGivenClientService.currentForm();
    return form?.().dirty() ?? false;
  });
}
