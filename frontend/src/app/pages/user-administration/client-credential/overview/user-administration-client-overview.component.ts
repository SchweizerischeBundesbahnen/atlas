import { Component, OnDestroy, OnInit } from '@angular/core';
import { ClientCredential, UserAdministrationService } from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { TableSettings } from '../../../../core/components/table/table-settings';
import { Subscription } from 'rxjs';
import { tableColumns } from './table-column-definition';

@Component({
  selector: 'app-client-credential-administration-overview',
  templateUrl: './user-administration-client-overview.component.html',
})
export class UserAdministrationClientOverviewComponent implements OnInit, OnDestroy {
  isLoading = false;
  clientCredentials: ClientCredential[] = [];
  totalCount = 0;

  private credentialsSubscription!: Subscription;
  readonly tableColumns = tableColumns;

  constructor(
    private userAdministrationService: UserAdministrationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getOverview({
      page: 0,
      size: 10,
      sort: 'clientCredentialId,ASC',
    });
  }

  getOverview($paginationAndSearch: TableSettings) {
    this.isLoading = true;
    this.credentialsSubscription = this.userAdministrationService
      .getClientCredentials($paginationAndSearch.page, $paginationAndSearch.size, [
        $paginationAndSearch.sort!,
        'clientCredentialId,ASC',
      ])
      .subscribe((clientContainer) => {
        this.clientCredentials = clientContainer.objects!;
        this.totalCount = clientContainer.totalCount!;
        this.isLoading = false;
      });
  }

  edit(client: ClientCredential) {
    this.router.navigate([client.clientCredentialId], { relativeTo: this.route }).then();
  }

  ngOnDestroy() {
    this.credentialsSubscription.unsubscribe();
  }
}
