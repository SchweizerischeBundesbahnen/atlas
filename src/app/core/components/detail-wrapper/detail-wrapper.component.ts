import { Component, Input } from '@angular/core';
import { DetailWrapperController } from './detail-wrapper-controller';

@Component({
  selector: 'app-detail-wrapper [controller][heading][headingNew]',
  templateUrl: './detail-wrapper.component.html',
  styleUrls: ['./detail-wrapper.component.scss'],
})
export class DetailWrapperComponent {
  @Input() controller!: DetailWrapperController;
  @Input() heading: string | undefined;
  @Input() headingNew!: string;
  @Input() canEdit = true;
}
