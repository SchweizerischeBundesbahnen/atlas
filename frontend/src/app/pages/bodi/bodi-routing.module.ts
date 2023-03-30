import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { BodiOverviewComponent } from './overview/bodi-overview.component';
import { BusinessOrganisationDetailComponent } from './business-organisations/detail/business-organisation-detail.component';
import { Pages } from '../pages';
import { RouteToDialogComponent } from '../../core/components/route-to-dialog/route-to-dialog.component';
import { BusinessOrganisationComponent } from './business-organisations/business-organisation.component';
import { TransportCompaniesComponent } from './transport-companies/transport-companies.component';
import { TransportCompanyDetailComponent } from './transport-companies/detail/transport-company-detail.component';
import { TransportCompanyDetailResolver } from './transport-companies/detail/transport-company-detail-resolver.service';
import { CompaniesComponent } from './companies/companies.component';
import { CompanyDetailComponent } from './companies/detail/company-detail.component';
import { CompanyDetailResolver } from './companies/detail/company-detail-resolver.service';
import { BusinessOrganisationDetailResolver } from './business-organisations/detail/business-organisation-detail-resolver.service';

const routes: Routes = [
  {
    path: '',
    component: BodiOverviewComponent,
    children: [
      {
        path: Pages.BUSINESS_ORGANISATIONS.path,
        component: BusinessOrganisationComponent,
        children: [
          {
            path: ':id',
            component: RouteToDialogComponent,
            data: { component: BusinessOrganisationDetailComponent },
            resolve: {
              businessOrganisationDetail: BusinessOrganisationDetailResolver,
            },
            runGuardsAndResolvers: 'always',
          },
        ],
      },
      {
        path: Pages.TRANSPORT_COMPANIES.path,
        component: TransportCompaniesComponent,
        children: [
          {
            path: ':id',
            component: RouteToDialogComponent,
            data: { component: TransportCompanyDetailComponent },
            resolve: {
              transportCompanyDetail: TransportCompanyDetailResolver,
            },
            runGuardsAndResolvers: 'always',
          },
        ],
      },
      {
        path: Pages.COMPANIES.path,
        component: CompaniesComponent,
        children: [
          {
            path: ':id',
            component: RouteToDialogComponent,
            data: { component: CompanyDetailComponent },
            resolve: {
              companyDetail: CompanyDetailResolver,
            },
            runGuardsAndResolvers: 'always',
          },
        ],
      },
      { path: '**', redirectTo: Pages.BUSINESS_ORGANISATIONS.path },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BodiRoutingModule {}
