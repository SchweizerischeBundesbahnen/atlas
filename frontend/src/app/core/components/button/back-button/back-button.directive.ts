import { Directive, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Directive({ selector: '[backButton]' })
export class BackButtonDirective {
  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  @HostListener('click')
  onClick(): void {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }
}
