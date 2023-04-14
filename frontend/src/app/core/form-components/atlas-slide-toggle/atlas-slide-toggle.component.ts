import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'atlas-slide-toggle',
  templateUrl: './atlas-slide-toggle.component.html',
  styleUrls: ['./atlas-slide-toggle.component.scss'],
})
export class AtlasSlideToggleComponent {
  @Input() toggle = false;
  @Output() toggleChange = new EventEmitter<boolean>();

  handleToggleClick(): void {
    this.toggle = !this.toggle;
    this.toggleChange.emit(this.toggle);
  }
}
