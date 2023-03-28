import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';
import { UserAdministrationUserEditComponent } from './user/detail/edit/user-administration-user-edit.component';
import { UserAdministrationUserCreateComponent } from './user/detail/create/user-administration-user-create.component';
import { UserAdministrationApplicationConfigComponent } from './components/application-config/user-administration-application-config.component';
import { UserSelectComponent } from './user/user-select/user-select.component';
import { UserAdministrationReadOnlyDataComponent } from './components/read-only-data/user-administration-read-only-data.component';
import { UserAdministrationUserDetailComponent } from './user/detail/user-administration/user-administration-user-detail.component';
import { EditTitlePipe } from './user/detail/edit/edit-title.pipe';
import { UserSelectFormatPipe } from './user/user-select/user-select-format.pipe';
import { FormsModule } from '@angular/forms';
import { UserAdministrationClientDetailComponent } from './client-credential/detail/user-administration-client-detail.component';
import { UserAdministrationClientCreateComponent } from './client-credential/detail/create/user-administration-client-create.component';
import { UserAdministrationUserOverviewComponent } from './user/overview/user-administration-overview.component';
import { UserAdministrationClientOverviewComponent } from './client-credential/overview/client-credential-administration-overview.component';
import { UserAdministrationClientEditComponent } from './client-credential/detail/edit/client-credential-administration-client-edit.component';

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,

    UserAdministrationUserOverviewComponent,
    UserAdministrationUserDetailComponent,
    UserAdministrationUserEditComponent,
    UserAdministrationUserCreateComponent,

    UserAdministrationClientOverviewComponent,
    UserAdministrationClientDetailComponent,
    UserAdministrationClientCreateComponent,
    UserAdministrationClientEditComponent,

    UserAdministrationApplicationConfigComponent,
    UserSelectComponent,
    UserAdministrationReadOnlyDataComponent,
    EditTitlePipe,
    UserSelectFormatPipe,
  ],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule, FormsModule],
})
export class UserAdministrationModule {}
