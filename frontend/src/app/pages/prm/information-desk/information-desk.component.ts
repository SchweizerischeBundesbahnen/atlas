import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';

@Component({
  selector: 'app-information-desk',
  templateUrl: './information-desk.component.html',
  styleUrls: ['./information-desk.component.scss'],
})
export class InformationDeskComponent extends BasePrmComponentService implements OnInit {
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
