import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/module/core.module';
import { PagesModule } from './pages/module/pages.module';

@NgModule({
  declarations: [AppComponent],
  imports: [CoreModule, PagesModule, AppRoutingModule],
  bootstrap: [AppComponent],
})
export class AppModule {}
