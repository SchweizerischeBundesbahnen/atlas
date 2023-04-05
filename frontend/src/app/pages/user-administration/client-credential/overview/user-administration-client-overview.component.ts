import { Component, OnDestroy } from '@angular/core';
import { ClientCredential, UserAdministrationService } from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { tableColumns } from './table-column-definition';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { TableService } from '../../../../core/components/table/table.service';

@Component({
  selector: 'app-client-credential-administration-overview',
  templateUrl: './user-administration-client-overview.component.html',
  providers: [TableService],
})
export class UserAdministrationClientOverviewComponent implements OnDestroy {
  clientCredentials: ClientCredential[] = [];
  totalCount = 0;

  readonly tableColumns = tableColumns;

  private credentialsSubscription?: Subscription;

  constructor(
    private userAdministrationService: UserAdministrationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  getOverview(pagination: TablePagination) {
    this.credentialsSubscription = this.userAdministrationService
      .getClientCredentials(pagination.page, pagination.size, [
        pagination.sort!,
        'clientCredentialId,asc',
      ])
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
