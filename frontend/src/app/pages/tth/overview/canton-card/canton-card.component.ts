import { Component, Input } from '@angular/core';
import { Canton } from '../../../../core/cantons/Canton';
import { TranslatePipe } from '@ngx-translate/core';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'atlas-canton-card',
  templateUrl: './canton-card.component.html',
  styleUrls: ['./canton-card.component.scss'],
  imports: [TranslatePipe, NgOptimizedImage],
  providers: [TranslatePipe],
})
export class CantonCardComponent {

  @Input() canton!: Canton;
}
