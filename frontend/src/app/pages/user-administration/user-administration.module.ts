import { NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';
import { UserAdministrationEditComponent } from './detail/edit/user-administration-edit.component';
import { UserAdministrationCreateComponent } from './detail/create/user-administration-create.component';
import { UserAdministrationApplicationConfigComponent } from './application-config/user-administration-application-config.component';
import { UserSelectComponent } from './user-select/user-select.component';
import { UserAdministrationReadOnlyDataComponent } from './read-only-data/user-administration-read-only-data.component';
import { UserAdministrationComponent } from './detail/user-administration/user-administration.component';
import { EditTitlePipe } from './detail/edit/edit-title.pipe';
import { UserSelectFormatPipe } from './user-select/user-select-format.pipe';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,
    UserAdministrationEditComponent,
    UserAdministrationCreateComponent,
    UserAdministrationApplicationConfigComponent,
    UserSelectComponent,
    UserAdministrationReadOnlyDataComponent,
    UserAdministrationComponent,
    EditTitlePipe,
    UserSelectFormatPipe,
  ],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule, FormsModule],
})
export class UserAdministrationModule {}
