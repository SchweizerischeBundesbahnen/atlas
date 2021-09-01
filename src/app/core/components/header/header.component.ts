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
  private readonly INT: string = 'int';

  version: string = packageJson.version;
  environmentLabel: string = environment.label;

  @Output() showSidebar = new EventEmitter<void>();

  toggle(): void {
    this.showSidebar.emit();
  }

  showLabel() {
    return this.environmentLabel === this.DEV || this.environmentLabel === this.INT;
  }

  getEnvLabelClass() {
    const envClass = {
      badge: true,
      'text-wrap': true,
      'ms-2': true,
      'bg-primary': false,
      'bg-warning': false,
    };
    if (this.environmentLabel === this.DEV) {
      envClass['bg-primary'] = true;
      return envClass;
    }
    if (this.environmentLabel === this.INT) {
      envClass['bg-warning'] = true;
      return envClass;
    }
    return envClass;
  }
}
