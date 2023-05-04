import { Directive, HostBinding, HostListener, Input } from '@angular/core';
import { isEmpty } from '../../util/strings';
import { Observable, of } from 'rxjs';

@Directive({
  selector: '[mouseOverTitle]',
})
export class MouseOverTitleDirective {
  @Input() mouseOverTitle: (value: string) => Observable<string> = () => of('');
  @Input() mouseOverTitleValue = '';

  private oldValue = '';

  @HostBinding('title') title = '';

  @HostListener('mouseover') onMouseOver(): void {
    if (isEmpty(this.mouseOverTitleValue) || this.oldValue === this.mouseOverTitleValue) {
      return;
    }

    this.mouseOverTitle(this.mouseOverTitleValue).subscribe({
      next: (result) => {
        this.title = result;
        this.oldValue = this.mouseOverTitleValue;
      },
      error: (err) => {
        this.title = '';
        console.error(err);
      },
    });
  }
}
