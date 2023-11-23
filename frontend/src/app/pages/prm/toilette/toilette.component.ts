import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BasePrmComponentService } from '../base-prm-component.service';

@Component({
  selector: 'app-toilette',
  templateUrl: './toilette.component.html',
  styleUrls: ['./toilette.component.scss'],
})
export class ToiletteComponent extends BasePrmComponentService {
  constructor(readonly router: Router) {
    super(router);
  }
}
