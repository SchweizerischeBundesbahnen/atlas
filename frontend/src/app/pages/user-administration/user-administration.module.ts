import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';
import { UserAdministrationEditComponent } from './edit/user-administration-edit.component';
import { UserAdministrationCreateComponent } from './create/user-administration-create.component';
import { UserAdministrationApplicationConfigComponent } from './application-config/user-administration-application-config.component';
import { UserSelectComponent } from './user-select/user-select.component';
import { UserAdministrationReadOnlyDataComponent } from './read-only-data/user-administration-read-only-data.component';
import { UserAdministrationBasicComponent } from './basic/user-administration-basic.component';

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,
    UserAdministrationEditComponent,
    UserAdministrationCreateComponent,
    UserAdministrationApplicationConfigComponent,
    UserSelectComponent,
    UserAdministrationReadOnlyDataComponent,
    UserAdministrationBasicComponent,
  ],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule],
})
export class UserAdministrationModule {}
