import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { Pages } from '../pages';

import { transportCompanyResolver } from './transport-companies/detail/transport-company-detail-resolver.service';
import { companyResolver } from './companies/detail/company-detail-resolver.service';
import { businessOrganisationResolver } from './business-organisations/detail/business-organisation-detail-resolver.service';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

export const routes: Routes = [
  {
    path: Pages.BUSINESS_ORGANISATIONS.path + '/:id',
    loadComponent: () =>
      import(
        './business-organisations/detail/business-organisation-detail.component'
      ).then((m) => m.BusinessOrganisationDetailComponent),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      businessOrganisationDetail: businessOrganisationResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.TRANSPORT_COMPANIES.path + '/:id',
    loadComponent: () =>
      import(
        './transport-companies/detail/transport-company-detail.component'
      ).then((m) => m.TransportCompanyDetailComponent),
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      transportCompanyDetail: transportCompanyResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.COMPANIES.path + '/:id',
    loadComponent: () =>
      import('./companies/detail/company-detail.component').then(
        (m) => m.CompanyDetailComponent
      ),
    resolve: {
      companyDetail: companyResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    loadComponent: () =>
      import('./overview/bodi-overview.component').then(
        (m) => m.BodiOverviewComponent
      ),
    children: [
      {
        path: Pages.BUSINESS_ORGANISATIONS.path,
        loadComponent: () =>
          import(
            './business-organisations/business-organisation.component'
          ).then((m) => m.BusinessOrganisationComponent),
      },
      {
        path: Pages.TRANSPORT_COMPANIES.path,
        loadComponent: () =>
          import('./transport-companies/transport-companies.component').then(
            (m) => m.TransportCompaniesComponent
          ),
      },
      {
        path: Pages.COMPANIES.path,
        loadComponent: () =>
          import('./companies/companies.component').then(
            (m) => m.CompaniesComponent
          ),
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
export class BodiRouting {}
