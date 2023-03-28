import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';
import { UserAdministrationEditComponent } from './user/detail/edit/user-administration-edit.component';
import { UserAdministrationCreateComponent } from './user/detail/create/user-administration-create.component';
import { UserAdministrationApplicationConfigComponent } from './components/application-config/user-administration-application-config.component';
import { UserSelectComponent } from './user/user-select/user-select.component';
import { UserAdministrationReadOnlyDataComponent } from './components/read-only-data/user-administration-read-only-data.component';
import { UserAdministrationComponent } from './user/detail/user-administration/user-administration.component';
import { EditTitlePipe } from './user/detail/edit/edit-title.pipe';
import { UserSelectFormatPipe } from './user/user-select/user-select-format.pipe';
import { FormsModule } from '@angular/forms';
import { ClientCredentialAdministrationComponent } from './client-credential/detail/client-credential-administration.component';
import { ClientCredentialAdministrationCreateComponent } from './client-credential/detail/create/client-credential-administration-create.component';
import { UserAdministrationUserOverviewComponent } from './user/overview/user-administration-overview.component';
import { UserAdministrationClientsOverviewComponent } from './client-credential/overview/client-credential-administration-overview.component';
import { UserAdministrationClientEditComponent } from './client-credential/detail/edit/client-credential-administration-client-edit.component';

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,
    UserAdministrationUserOverviewComponent,
    UserAdministrationEditComponent,
    UserAdministrationCreateComponent,
    UserAdministrationApplicationConfigComponent,
    UserSelectComponent,
    UserAdministrationReadOnlyDataComponent,
    UserAdministrationComponent,
    EditTitlePipe,
    UserSelectFormatPipe,
    ClientCredentialAdministrationComponent,
    ClientCredentialAdministrationCreateComponent,
    UserAdministrationClientsOverviewComponent,
    UserAdministrationClientEditComponent,
  ],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule, FormsModule],
})
export class UserAdministrationModule {}
