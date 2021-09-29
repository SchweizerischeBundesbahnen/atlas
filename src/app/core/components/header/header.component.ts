import { Component, EventEmitter, Output } from '@angular/core';
import packageJson from '../../../../../package.json';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private readonly DEV: string = 'dev';
  private readonly TEST: string = 'test';
  private readonly INT: string = 'int';
  private readonly STAGES_WITH_LABEL = [this.DEV, this.TEST, this.INT];

  version: string = packageJson.version;
  environmentLabel: string = environment.label;

  @Output() showSidebar = new EventEmitter<void>();

  toggle(): void {
    this.showSidebar.emit();
  }

  showLabel() {
    return this.STAGES_WITH_LABEL.includes(this.environmentLabel);
  }

  getEnvLabelClass() {
    return {
      badge: true,
      'text-wrap': true,
      'ms-2': true,
      'bg-primary': this.environmentLabel === this.DEV,
      'bg-secondary': this.environmentLabel === this.TEST,
      'bg-warning': this.environmentLabel === this.INT,
    };
  }
}
