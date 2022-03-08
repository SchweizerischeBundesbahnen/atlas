import { NgModule } from '@angular/core';
import { CmykPickerComponent } from './cmyk/cmyk-picker.component';
import { RgbPickerComponent } from './rgb/rgb-picker.component';
import { CoreModule } from '../../../core/module/core.module';
import { ColorPickerModule } from 'ngx-color-picker';

@NgModule({
  declarations: [CmykPickerComponent, RgbPickerComponent],
  exports: [CmykPickerComponent, RgbPickerComponent],
  imports: [CoreModule, ColorPickerModule],
})
export class ColorModule {}
