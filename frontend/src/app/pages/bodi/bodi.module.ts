import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { BusinessOrganisationComponent } from './business-organisations/business-organisation.component';
import { BodiRoutingModule } from './bodi-routing.module';
import { BodiOverviewComponent } from './overview/bodi-overview.component';
import { BusinessOrganisationDetailComponent } from './business-organisations/detail/business-organisation-detail.component';
import { FormModule } from '../../core/module/form.module';

@NgModule({
  declarations: [
    BodiOverviewComponent,
    BusinessOrganisationComponent,
    BusinessOrganisationDetailComponent,
  ],
  imports: [CoreModule, BodiRoutingModule, FormModule],
})
export class BodiModule {}
