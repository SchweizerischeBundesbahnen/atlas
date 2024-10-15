import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";

export enum NavigationToIconType {
  PRM = 'prm',
  SEPODI = 'sepodi'
}

@Component({
  selector: 'app-navigation-sepodi-prm',
  templateUrl: './navigation-sepodi-prm.component.html'
})
export class NavigationSepodiPrmComponent {

  @Input() url!: string[];
  @Input() icon!: NavigationToIconType;

  constructor(private router: Router) {}

  navigate(url: string[]) {
    this.router.navigate(url);
  }
}
