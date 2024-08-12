import {NgModule} from "@angular/core";
import {MassimportOverviewComponent} from "./overview/massimport-overview.component";
import {CoreModule} from "../../core/module/core.module";
import {ColorModule} from "../lidi/color-picker/color.module";
import {MassimportRoutingModule} from "./massimport-routing.module";
import {FormModule} from "../../core/module/form.module";

@NgModule({
  declarations: [
    MassimportOverviewComponent,
  ],
  imports: [CoreModule, ColorModule, MassimportRoutingModule, FormModule],
})
export class MassimportModule {}
