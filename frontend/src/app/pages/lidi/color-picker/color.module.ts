import { NgModule } from '@angular/core';
import { CmykPickerComponent } from './cmyk/cmyk-picker.component';
import { RgbPickerComponent } from './rgb/rgb-picker.component';
import { CoreModule } from '../../../core/module/core.module';
import { ColorPickerModule } from 'ngx-color-picker';
import { FormModule } from '../../../core/module/form.module';

@NgModule({
  declarations: [CmykPickerComponent, RgbPickerComponent],
  exports: [CmykPickerComponent, RgbPickerComponent],
  imports: [CoreModule, ColorPickerModule, FormModule],
})
export class ColorModule {}
