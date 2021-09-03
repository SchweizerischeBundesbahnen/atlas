import { Component, Input } from '@angular/core';
import { DetailWrapperController } from './detail-wrapper-controller';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './detail-wrapper.component.html',
  styleUrls: ['./detail-wrapper.component.scss'],
})
export class DetailWrapperComponent<TYPE> {
  @Input() controller!: DetailWrapperController<TYPE>;
  @Input() headingNew!: string;
  @Input() canEdit = true;
}
