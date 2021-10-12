import { NgModule } from '@angular/core';
import { HomeComponent } from '../home/home.component';
import { CoreModule } from '../../core/module/core.module';
import { LidiModule } from '../lidi/lidi.module';
import { TtfnModule } from '../ttfn/ttfn.module';
import { AppRoutingModule } from '../../app-routing.module';

@NgModule({
  declarations: [HomeComponent],
  imports: [CoreModule, LidiModule, TtfnModule, AppRoutingModule],
})
export class PagesModule {}
