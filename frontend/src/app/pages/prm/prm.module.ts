import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PrmHomeSearchComponent} from './prm-home-search/prm-home-search.component';
import {FormModule} from '../../core/module/form.module';
import {FormsModule} from '@angular/forms';
import {PrmRoutingModule} from './prm-routing.module';
import {StopPointDetailComponent} from './tabs/stop-point/detail/stop-point-detail.component';
import {CoreModule} from '../../core/module/core.module';
import {PrmPanelComponent} from './prm-panel/prm-panel.component';
import {StopPointReducedFormComponent} from './tabs/stop-point/form/stop-point-reduced-form/stop-point-reduced-form.component';
import {StopPointCompleteFormComponent} from './tabs/stop-point/form/stop-point-complete-form/stop-point-complete-form.component';
import {ReferencePointTableComponent} from './tabs/reference-point/reference-point-table.component';
import {PlatformTableComponent} from './tabs/platform/platform-table.component';
import {ToiletComponent} from './tabs/toilet/toilet.component';
import {CreateStopPointComponent} from './tabs/stop-point/create-stop-point/create-stop-point.component';
import {PrmInfoBoxComponent} from './prm-home-search/prm-info-box/prm-info-box.component';
import {PlatformCompleteFormComponent} from './tabs/platform/detail/form/platform-complete-form/platform-complete-form.component';
import {PlatformReducedFormComponent} from './tabs/platform/detail/form/platform-reduced-form/platform-reduced-form.component';
import {PlatformDetailPanelComponent} from './tabs/platform/detail/platform-detail-panel.component';
import {
  ReferencePointCompleteFormComponent
} from './tabs/reference-point/detail/form/reference-point-complete-form/reference-point-complete-form.component';
import {ReferencePointDetailComponent} from './tabs/reference-point/detail/reference-point-detail.component';
import {ParkingLotTableComponent} from './tabs/parking-lot/parking-lot-table.component';
import {ParkingLotDetailComponent} from './tabs/parking-lot/detail/parking-lot-detail.component';
import {ParkingLotFormComponent} from './tabs/parking-lot/detail/form/parking-lot-form/parking-lot-form.component';
import {ContactPointTableComponent} from './tabs/contact-point/contact-point-table.component';
import {ContactPointDetailComponent} from './tabs/contact-point/detail/contact-point-detail.component';
import {ContactPointFormComponent} from './tabs/contact-point/detail/form/contact-point-form/contact-point-form.component';
import {ToiletDetailComponent} from "./tabs/toilet/detail/toilet-detail.component";
import {ToiletFormComponent} from "./tabs/toilet/detail/form/toilet-form/toilet-form.component";
import {RelationTabDetailComponent} from "./tabs/relation/tab-detail/relation-tab-detail.component";
import {RelationDetailComponent} from "./tabs/relation/detail/relation-detail.component";
import {DetailWithRelationTabComponent} from "./tabs/relation/tab/detail-with-relation-tab.component";
import {PlatformDetailComponent} from "./tabs/platform/detail/detail/platform-detail.component";

@NgModule({
  declarations: [
    PrmHomeSearchComponent,
    PrmInfoBoxComponent,
    PrmPanelComponent,
    StopPointDetailComponent,
    CreateStopPointComponent,
    StopPointReducedFormComponent,
    StopPointCompleteFormComponent,
    ToiletComponent,
    ToiletDetailComponent,
    ToiletFormComponent,
    PlatformTableComponent,
    PlatformDetailPanelComponent,
    PlatformDetailComponent,
    PlatformCompleteFormComponent,
    PlatformReducedFormComponent,
    ReferencePointTableComponent,
    ReferencePointDetailComponent,
    ReferencePointCompleteFormComponent,
    ParkingLotTableComponent,
    ParkingLotDetailComponent,
    ParkingLotFormComponent,
    ContactPointTableComponent,
    ContactPointDetailComponent,
    ContactPointFormComponent,
    DetailWithRelationTabComponent,
    RelationTabDetailComponent,
    RelationDetailComponent,
  ],
  imports: [CommonModule, FormModule, FormsModule, PrmRoutingModule, CoreModule],
})
export class PrmModule {}
