import { Component, input } from '@angular/core';
import { AtlasClipboardComponent } from '../form-components/atlas-clipboard/atlas-clipboard.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-sloid-container',
  templateUrl: './sloid-container.component.html',
  imports: [AtlasClipboardComponent, TranslatePipe],
  providers: [TranslatePipe],
})
export class SloidContainerComponent {
  readonly sloid = input.required<string>();
  readonly label = input<string>('SEPODI.SERVICE_POINTS.SLOID');
}
