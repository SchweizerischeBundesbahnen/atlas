import {
  AfterViewInit,
  Component,
  ContentChildren,
  Input,
  QueryList,
  ViewChild,
} from '@angular/core';
import { DetailWrapperController } from './detail-wrapper-controller';
import { FormGroup, NgForm, NgModel } from '@angular/forms';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './detail-wrapper.component.html',
  styleUrls: ['./detail-wrapper.component.scss'],
})
export class DetailWrapperComponent {
  @Input() controller!: DetailWrapperController<any>;
  @Input() headingNew!: string;
  @Input() canEdit = true;
}
