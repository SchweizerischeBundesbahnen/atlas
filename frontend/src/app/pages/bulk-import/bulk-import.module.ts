import {NgModule} from "@angular/core";
import {BulkImportOverviewComponent} from "./overview/bulk-import-overview.component";
import {CoreModule} from "../../core/module/core.module";
import {ColorModule} from "../lidi/color-picker/color.module";
import {BulkImportRoutingModule} from "./bulk-import-routing.module";
import {FormModule} from "../../core/module/form.module";

@NgModule({
  declarations: [
    BulkImportOverviewComponent,
  ],
  imports: [CoreModule, ColorModule, BulkImportRoutingModule, FormModule],
})
export class BulkImportModule {}
