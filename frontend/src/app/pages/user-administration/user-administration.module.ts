import { InjectionToken, NgModule } from '@angular/core';
import { UserAdministrationOverviewComponent } from './overview/user-administration-overview.component';
import { UserAdministrationRoutingModule } from './user-administration-routing.module';
import { CoreModule } from '../../core/module/core.module';
import { FormModule } from '../../core/module/form.module';
import { UserAdministrationEditComponent } from './detail/edit/user-administration-edit.component';
import { UserAdministrationCreateComponent } from './detail/create/user-administration-create.component';
import { UserAdministrationApplicationConfigComponent } from './application-config/user-administration-application-config.component';
import { UserAdministrationReadOnlyDataComponent } from './read-only-data/user-administration-read-only-data.component';
import { UserAdministrationComponent } from './detail/user-administration/user-administration.component';
import { EditTitlePipe } from './pipes/edit-title.pipe';
import { UserSelectFormatPipe } from './pipes/user-select-format.pipe';
import { FormsModule } from '@angular/forms';
import { AtlasSearchSelectModule } from '../../core/form-components/atlas-search-select/atlas-search-select.module';
import { UserService } from './service/user.service';

//export const searchableService = new InjectionToken<UserService>('SEARCHABLE_USER_SERVICE');

@NgModule({
  declarations: [
    UserAdministrationOverviewComponent,
    UserAdministrationEditComponent,
    UserAdministrationCreateComponent,
    UserAdministrationApplicationConfigComponent,
    UserAdministrationReadOnlyDataComponent,
    UserAdministrationComponent,
    EditTitlePipe,
    UserSelectFormatPipe,
  ],
  imports: [
    UserAdministrationRoutingModule,
    CoreModule,
    FormModule,
    FormsModule,
    AtlasSearchSelectModule,
  ],
})
export class UserAdministrationModule {}
