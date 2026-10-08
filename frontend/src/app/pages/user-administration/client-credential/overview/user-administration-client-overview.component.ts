import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { ClientCredential } from '../../../../api';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tableColumns } from './table-column-definition';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../../core/util/arrays';
import { TableComponent } from '../../../../core/components/table/table.component';
import { ClientCredentialAdministrationService } from '../../../../api/service/user-administration/client-credential-administration.service';
import { applyIfDefined } from '../../../../core/util/values';

@Component({
  selector: 'atlas-client-credential-administration-overview',
  templateUrl: './user-administration-client-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TableComponent, RouterOutlet],
})
export class UserAdministrationClientOverviewComponent {
  readonly clientCredentials = signal<ClientCredential[]>([]);
  readonly totalCount = signal(0);

  readonly tableColumns = tableColumns;

  private credentialsSubscription?: Subscription;

  private readonly clientCredentialAdministrationService = inject(ClientCredentialAdministrationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  getOverview(pagination: TablePagination) {
    this.credentialsSubscription?.unsubscribe();
    this.credentialsSubscription = this.clientCredentialAdministrationService
      .getClientCredentials(
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'clientCredentialId,asc')
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((clientContainer) => {
        applyIfDefined(clientContainer.objects, this.clientCredentials.set);
        applyIfDefined(clientContainer.totalCount, this.totalCount.set);
      });
  }

  edit(client: ClientCredential) {
    this.router.navigate([client.clientCredentialId], { relativeTo: this.route }).then();
  }
}
