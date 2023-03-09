import { NgModule } from '@angular/core';
import { AtlasSearchSelectComponent } from './atlas-search-select.component';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [AtlasSearchSelectComponent],
  exports: [AtlasSearchSelectComponent],
  imports: [TranslateModule, CommonModule],
})
export class AtlasSearchSelectModule {}
