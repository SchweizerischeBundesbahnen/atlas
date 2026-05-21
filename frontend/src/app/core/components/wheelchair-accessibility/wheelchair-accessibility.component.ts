import { Component, input } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { InfoIconComponent } from '@atlas/form';
import { TranslatePipe } from '@ngx-translate/core';
import { ReadWheelchairAccessibility } from '../../../api';

@Component({
  selector: 'atlas-wheelchair-accessibility',
  imports: [NgOptimizedImage, InfoIconComponent, TranslatePipe],
  templateUrl: './wheelchair-accessibility.component.html',
})
export class WheelchairAccessibilityComponent {
  wheelchairAccessibility = input<ReadWheelchairAccessibility.StateEnum>(ReadWheelchairAccessibility.StateEnum.NoInfo);
  isSelectedVersionValidToday = input<boolean>(false);
}
