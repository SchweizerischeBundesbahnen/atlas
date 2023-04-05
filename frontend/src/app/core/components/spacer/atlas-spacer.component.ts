import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'atlas-spacer',
  templateUrl: './atlas-spacer.component.html',
  styleUrls: ['atlas-spacer.component.scss'],
})
export class AtlasSpacerComponent implements OnInit {
  @Input() height!: string;
  @Input() wrapperStyleClass!: string;
  @Input() divider = false;
  styleClasses: string[] = ['spacer'];

  ngOnInit(): void {
    if (this.divider) {
      this.styleClasses.push('divider');
    }
  }
}
