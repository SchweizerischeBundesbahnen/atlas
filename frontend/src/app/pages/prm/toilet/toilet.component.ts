import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';

@Component({
  selector: 'app-toilet',
  templateUrl: './toilet.component.html',
  styleUrls: ['./toilet.component.scss'],
})
export class ToiletComponent extends BasePrmComponentService implements OnInit {
  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.checkStopPointExists(this.route.parent!.snapshot!.data!);
  }
}
