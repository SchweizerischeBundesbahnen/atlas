import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { BodiOverviewComponent } from './overview/bodi-overview.component';
import { BusinessOrganisationDetailComponent } from './business-organisations/detail/business-organisation-detail.component';
import { Pages } from '../pages';
import { BusinessOrganisationComponent } from './business-organisations/business-organisation.component';
import { TransportCompaniesComponent } from './transport-companies/transport-companies.component';
import { TransportCompanyDetailComponent } from './transport-companies/detail/transport-company-detail.component';
import { transportCompanyResolver } from './transport-companies/detail/transport-company-detail-resolver.service';
import { CompaniesComponent } from './companies/companies.component';
import { CompanyDetailComponent } from './companies/detail/company-detail.component';
import { companyResolver } from './companies/detail/company-detail-resolver.service';
import { businessOrganisationResolver } from './business-organisations/detail/business-organisation-detail-resolver.service';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

const routes: Routes = [
  {
    path: Pages.BUSINESS_ORGANISATIONS.path + '/:id',
    component: BusinessOrganisationDetailComponent,
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      businessOrganisationDetail: businessOrganisationResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.TRANSPORT_COMPANIES.path + '/:id',
    component: TransportCompanyDetailComponent,
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      transportCompanyDetail: transportCompanyResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.COMPANIES.path + '/:id',
    component: CompanyDetailComponent,
    resolve: {
      companyDetail: companyResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    component: BodiOverviewComponent,
    children: [
      {
        path: Pages.BUSINESS_ORGANISATIONS.path,
        component: BusinessOrganisationComponent,
      },
      {
        path: Pages.TRANSPORT_COMPANIES.path,
        component: TransportCompaniesComponent,
      },
      {
        path: Pages.COMPANIES.path,
        component: CompaniesComponent,
      },
      { path: '**', redirectTo: Pages.BUSINESS_ORGANISATIONS.path },
    ],
  },
  { path: '**', redirectTo: Pages.BODI.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BodiRoutingModule {}
