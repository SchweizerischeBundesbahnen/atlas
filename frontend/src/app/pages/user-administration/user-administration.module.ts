import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';

@NgModule({
  declarations: [UserAdministrationOverviewComponent],
  imports: [UserAdministrationRoutingModule, CoreModule],
})
export class UserAdministrationModule {}
