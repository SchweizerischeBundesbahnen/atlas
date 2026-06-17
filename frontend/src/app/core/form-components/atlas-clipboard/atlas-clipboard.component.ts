import { ChangeDetectionStrategy, Component, HostListener, inject, Input, input } from '@angular/core';
import { NotificationService } from '../../notification/notification.service';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-clipboard',
  templateUrl: './atlas-clipboard.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './atlas-clipboard.component.scss',
  imports: [CdkCopyToClipboard, TranslatePipe],
  providers: [TranslatePipe],
})
export class AtlasClipboardComponent {
  @Input() value: string | undefined;
  readonly showMe = input(true);

  private readonly notificationService = inject(NotificationService);

  @HostListener('click')
  onClick() {
    this.notificationService.success('COMMON.COPY_CLIPBOARD_SUCCESS');
  }
}
