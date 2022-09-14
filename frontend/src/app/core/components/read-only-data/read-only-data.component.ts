import { Component, Input } from '@angular/core';
import { ReadOnlyData } from './read-only-data';

@Component({
  selector: 'app-read-only-data',
  templateUrl: './read-only-data.component.html',
  styleUrls: ['read-only-data.component.scss'],
})
export class ReadOnlyDataComponent<T> {
  @Input() config!: ReadOnlyData<T>[][];
  @Input() data!: T;
}
