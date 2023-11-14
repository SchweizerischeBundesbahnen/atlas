import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PrmSearchOverviewComponent} from './prm-overview/prm-search-overview.component';
import {FormModule} from '../../core/module/form.module';
import {FormsModule} from '@angular/forms';
import {PrmRoutingModule} from './prm-routing.module';
import {SepodiModule} from "../sepodi/sepodi.module";
import {PrmDetailPanelComponent} from './prm-detail-panel/prm-detail-panel.component';
import {CoreModule} from "../../core/module/core.module";
import { PrmPanelComponent } from './prm-panel/prm-panel.component';

@NgModule({
  declarations: [PrmSearchOverviewComponent, PrmDetailPanelComponent, PrmPanelComponent],
  imports: [CommonModule, FormModule, FormsModule, PrmRoutingModule, SepodiModule, CoreModule],
})
export class PrmModule {
}
