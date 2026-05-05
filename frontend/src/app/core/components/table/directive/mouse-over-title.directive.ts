import { Directive, HostBinding, HostListener, input } from '@angular/core';
import { isEmpty } from '../../../util/strings';
import { Observable, of } from 'rxjs';

@Directive({ selector: '[atlasMouseOverTitle]' })
export class MouseOverTitleDirective {
  readonly atlasMouseOverTitle = input<(value: string) => Observable<string>>(() => of(''));
  readonly mouseOverTitleValue = input('');

  private oldValue = '';

  @HostBinding('title') title = '';

  @HostListener('mouseover') onMouseOver(): void {
    const mouseOverTitleValue = this.mouseOverTitleValue();
    if (isEmpty(mouseOverTitleValue) || this.oldValue === mouseOverTitleValue) {
      return;
    }

    this.atlasMouseOverTitle()(mouseOverTitleValue).subscribe({
      next: (result) => {
        this.title = result;
        this.oldValue = this.mouseOverTitleValue();
      },
      error: (err) => {
        this.title = '';
        console.error(err);
      },
    });
  }
}
