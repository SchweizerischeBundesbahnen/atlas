import {NgModule} from '@angular/core';
import {BulkImportOverviewComponent} from './overview/bulk-import-overview.component';
import {CoreModule} from '../../core/module/core.module';
import {BulkImportRoutingModule} from './bulk-import-routing.module';
import {FormModule} from '../../core/module/form.module';
import {BulkImportLogComponent} from './log/bulk-import-log.component';
import {UserAdministrationModule} from '../user-administration/user-administration.module';
import {UserDisplayNamePipe} from '../../core/pipe/user-display-name.pipe';
import {ParamsForTranslationPipe} from '../../core/pipe/params-for-translation.pipe';

@NgModule({
  declarations: [BulkImportOverviewComponent, BulkImportLogComponent],
  imports: [
    CoreModule,
    BulkImportRoutingModule,
    FormModule,
    UserAdministrationModule,
    UserDisplayNamePipe,
    ParamsForTranslationPipe,
  ],
})
export class BulkImportModule {}
