import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';

@Component({
  selector: 'app-reference-point',
  templateUrl: './reference-point.component.html',
})
export class ReferencePointComponent extends BasePrmComponentService implements OnInit {
  constructor(
    protected readonly router: Router,
    private route: ActivatedRoute,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.checkIsReducedOrComplete(this.route.parent!.snapshot.data);
  }
}
