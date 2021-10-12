import { NgModule } from '@angular/core';
import { HomeComponent } from '../home/home.component';
import { CoreModule } from '../../core/module/core.module';
import { LidiModule } from '../lidi/lidi.module';
import { TtfnModule } from '../ttfn/ttfn.module';

@NgModule({
  declarations: [HomeComponent],
  imports: [CoreModule, LidiModule, TtfnModule],
})
export class PagesModule {}
