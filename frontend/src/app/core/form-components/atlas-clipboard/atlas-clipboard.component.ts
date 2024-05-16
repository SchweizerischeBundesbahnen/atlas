import {Component, HostListener, Input} from '@angular/core';
import {NotificationService} from "../../notification/notification.service";

@Component({
  selector: 'atlas-clipboard',
  templateUrl: './atlas-clipboard.component.html',
  styleUrl: './atlas-clipboard.component.scss'
})
export class AtlasClipboardComponent {

  @Input() value: string | undefined;
  @Input() showMe = true;

  constructor(private notificationService: NotificationService) {
  }

  @HostListener('click', ['$event'])
  public onClick(): void {
    this.notificationService.success('COMMON.COPY_CLIPBOARD_SUCCESS');
  }

}
