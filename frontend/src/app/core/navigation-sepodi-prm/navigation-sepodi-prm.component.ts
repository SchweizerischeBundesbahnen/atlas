import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-navigation-sepodi-prm',
  templateUrl: './navigation-sepodi-prm.component.html'
})
export class NavigationSepodiPrmComponent {

  @Input() navigateToSepodi!: boolean;
  @Input() navigate!: () => void;
}
