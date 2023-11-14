import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PrmOverviewComponent } from './prm-overview/prm-overview.component';
import { FormModule } from '../../core/module/form.module';
import { FormsModule } from '@angular/forms';
import { PrmRoutingModule } from './prm-routing.module';

@NgModule({
  declarations: [PrmOverviewComponent],
  imports: [CommonModule, FormModule, FormsModule, PrmRoutingModule],
})
export class PrmModule {}
