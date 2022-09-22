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
import { EditTitlePipe } from './edit/edit-title.pipe';
import { UserSelectFormatPipe } from './user-select/user-select-format.pipe';

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,
    UserAdministrationEditComponent,
    UserAdministrationCreateComponent,
    UserAdministrationApplicationConfigComponent,
    UserSelectComponent,
    UserAdministrationReadOnlyDataComponent,
    UserAdministrationBasicComponent,
    EditTitlePipe,
    UserSelectFormatPipe,
  ],
  imports: [UserAdministrationRoutingModule, CoreModule, FormModule],
})
export class UserAdministrationModule {}
