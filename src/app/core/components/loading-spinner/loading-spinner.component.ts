import { ChangeDetectionStrategy, Component, ElementRef, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadingSpinnerComponent implements OnInit {
  @Input() isLoading = false;
  @Input() fullScreen = false;

  constructor(private readonly element: ElementRef) {}

  ngOnInit(): void {
    this.element.nativeElement.parentElement.classList.add('has-overlay');
  }
}
