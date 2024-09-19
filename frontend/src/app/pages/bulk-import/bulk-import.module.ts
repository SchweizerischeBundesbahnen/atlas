import {NgModule} from "@angular/core";
import {BulkImportOverviewComponent} from "./overview/bulk-import-overview.component";
import {CoreModule} from "../../core/module/core.module";
import {ColorModule} from "../lidi/color-picker/color.module";
import {BulkImportRoutingModule} from "./bulk-import-routing.module";
import {FormModule} from "../../core/module/form.module";
import {UserAdministrationModule} from "../user-administration/user-administration.module";

@NgModule({
  declarations: [
    BulkImportOverviewComponent,
  ],
    imports: [CoreModule, ColorModule, BulkImportRoutingModule, FormModule, UserAdministrationModule],
})
export class BulkImportModule {}
