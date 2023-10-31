import { Component, OnDestroy } from '@angular/core';
import { ClientCredential, ClientCredentialAdministrationService } from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { tableColumns } from './table-column-definition';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../../core/util/arrays';

@Component({
  selector: 'app-client-credential-administration-overview',
  templateUrl: './user-administration-client-overview.component.html',
})
export class UserAdministrationClientOverviewComponent implements OnDestroy {
  clientCredentials: ClientCredential[] = [];
  totalCount = 0;

  readonly tableColumns = tableColumns;

  private credentialsSubscription?: Subscription;

  constructor(
    private clientCredentialAdministrationService: ClientCredentialAdministrationService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  getOverview(pagination: TablePagination) {
    this.credentialsSubscription = this.clientCredentialAdministrationService
      .getClientCredentials(
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'clientCredentialId,asc'),
      )
      .subscribe((clientContainer) => {
        this.clientCredentials = clientContainer.objects!;
        this.totalCount = clientContainer.totalCount!;
      });
  }

  edit(client: ClientCredential) {
    this.router.navigate([client.clientCredentialId], { relativeTo: this.route }).then();
  }

  ngOnDestroy() {
    this.credentialsSubscription?.unsubscribe();
  }
}
