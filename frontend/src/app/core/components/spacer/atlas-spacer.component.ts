import { Component, OnInit, input } from '@angular/core';

@Component({
  selector: 'atlas-spacer',
  templateUrl: './atlas-spacer.component.html',
  styleUrls: ['atlas-spacer.component.scss'],
})
export class AtlasSpacerComponent implements OnInit {
  readonly height = input('0');
  readonly divider = input(false);
  styleClasses: string[] = ['spacer'];

  ngOnInit(): void {
    if (this.divider()) {
      this.styleClasses.push('divider');
    }
  }
}
