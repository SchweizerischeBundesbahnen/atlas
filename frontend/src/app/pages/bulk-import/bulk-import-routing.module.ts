import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {BulkImportOverviewComponent} from "./overview/bulk-import-overview.component";
import {Pages} from "../pages";
import {canLeaveDirtyForm} from "../../core/leave-guard/leave-dirty-form-guard.service";

const routes: Routes = [
  {
    path: Pages.BULK_IMPORT.path,
    component: BulkImportOverviewComponent,
    canDeactivate: [canLeaveDirtyForm],
    runGuardsAndResolvers: 'always'
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BulkImportRoutingModule {}
