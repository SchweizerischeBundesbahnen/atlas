import { Directive, HostListener, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Directive({ selector: '[atlasBackButton]' })
export class BackButtonDirective {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  @HostListener('click')
  onClick() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }
}
