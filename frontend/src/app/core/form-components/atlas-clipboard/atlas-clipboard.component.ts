import { Component, HostListener, Input, inject, input } from '@angular/core';
import { NotificationService } from '../../notification/notification.service';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-clipboard',
  templateUrl: './atlas-clipboard.component.html',
  styleUrl: './atlas-clipboard.component.scss',
  imports: [CdkCopyToClipboard, TranslatePipe],
  providers: [TranslatePipe],
})
export class AtlasClipboardComponent {
  // TODO: Skipped for migration because:
  //  This input is used in a control flow expression (e.g. `@if` or `*ngIf`)
  //  and migrating would break narrowing currently.
  @Input() value: string | undefined;
  readonly showMe = input(true);

  private notificationService = inject(NotificationService);

  @HostListener('click')
  onClick() {
    this.notificationService.success('COMMON.COPY_CLIPBOARD_SUCCESS');
  }
}
