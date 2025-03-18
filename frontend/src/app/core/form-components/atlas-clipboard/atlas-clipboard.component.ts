import {Component, HostListener, Input} from '@angular/core';
import {NotificationService} from "../../notification/notification.service";
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'atlas-clipboard',
    templateUrl: './atlas-clipboard.component.html',
    styleUrl: './atlas-clipboard.component.scss',
    imports: [CdkCopyToClipboard, TranslatePipe]
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
