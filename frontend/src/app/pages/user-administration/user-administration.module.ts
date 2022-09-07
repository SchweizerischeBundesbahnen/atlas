import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';
import { UserAdministrationDetailComponent } from './detail/user-administration-detail.component';
import { UserAdministrationEditComponent } from './edit/user-administration-edit.component';
import { UserAdministrationCreateComponent } from './create/user-administration-create.component';
import { UserAdministrationApplicationConfigComponent } from './application-config/user-administration-application-config.component';

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,
    UserAdministrationDetailComponent,
    UserAdministrationEditComponent,
    UserAdministrationCreateComponent,
    UserAdministrationApplicationConfigComponent,
  ],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule],
})
export class UserAdministrationModule {}
