import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';

@NgModule({
  declarations: [UserAdministrationOverviewComponent],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule],
})
export class UserAdministrationModule {}
