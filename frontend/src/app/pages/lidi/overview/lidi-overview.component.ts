import {Component} from '@angular/core';
import {Pages} from "../../pages";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  templateUrl: './lidi-overview.component.html',
  styleUrls:['./lidi-overview.component.scss']
})
export class LidiOverviewComponent {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
  ) {
  }

  newLine() {
    this.router
      .navigate([Pages.LINES.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }

  newSubline() {
    this.router
      .navigate([Pages.SUBLINES.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }
}
