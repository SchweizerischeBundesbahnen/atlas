import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { BusinessOrganisationComponent } from './business-organisations/business-organisation.component';
import { BodiRoutingModule } from './bodi-routing.module';
import { BodiOverviewComponent } from './overview/bodi-overview.component';
import { BusinessOrganisationDetailComponent } from './business-organisations/detail/business-organisation-detail.component';
import { FormModule } from '../../core/module/form.module';
import { TransportCompaniesComponent } from './transport-companies/transport-companies.component';
import { TransportCompanyDetailComponent } from './transport-companies/detail/transport-company-detail.component';
import { CompaniesComponent } from './companies/companies.component';
import { CompanyDetailComponent } from './companies/detail/company-detail.component';
import { AtlasSearchSelectModule } from '../../core/form-components/atlas-search-select/atlas-search-select.module';

@NgModule({
  declarations: [
    BodiOverviewComponent,
    BusinessOrganisationComponent,
    BusinessOrganisationDetailComponent,
    TransportCompaniesComponent,
    TransportCompanyDetailComponent,
    CompaniesComponent,
    CompanyDetailComponent,
  ],
  imports: [CoreModule, BodiRoutingModule, FormModule, AtlasSearchSelectModule],
})
export class BodiModule {}
