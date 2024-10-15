import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-navigation-sepodi-prm',
  templateUrl: './navigation-sepodi-prm.component.html'
})
export class NavigationSepodiPrmComponent {

  @Input() url!: string[];
  @Input() showSepodiIcon!: boolean;
  @Input() showPrmIcon!: boolean;

  constructor(private router: Router) {}

  navigate(url: string[]) {
    this.router.navigate(url);
  }
}
