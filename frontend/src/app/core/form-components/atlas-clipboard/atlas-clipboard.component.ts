import {Component, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {NotificationService} from "../../notification/notification.service";

@Component({
  selector: 'atlas-clipboard',
  templateUrl: './atlas-clipboard.component.html',
  styleUrl: './atlas-clipboard.component.scss'
})
export class AtlasClipboardComponent {

  @Input() value: string | undefined;
  @Input() showMe = true;

  @Output()
  public readonly copied: EventEmitter<string> = new EventEmitter<string>();

  constructor(private notificationService: NotificationService) {
  }

  @HostListener('click', ['$event'])
  public onClick(event: MouseEvent): void {
    this.notificationService.success('COMMON.COPY_CLIPBOARD_SUCCESS');
  }

}
