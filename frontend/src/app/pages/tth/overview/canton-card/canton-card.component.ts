import { Component, Input } from '@angular/core';
import { Canton } from '../../../../core/cantons/Canton';

@Component({
  selector: 'atlas-canton-card',
  templateUrl: './canton-card.component.html',
  styleUrls: ['./canton-card.component.scss'],
})
export class CantonCardComponent {
  @Input() canton!: Canton;
}
