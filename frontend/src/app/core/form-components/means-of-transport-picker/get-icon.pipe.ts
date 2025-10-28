import { Pipe, PipeTransform } from '@angular/core';
import { MeanOfTransport } from '../../../api';

@Pipe({
  name: 'getIcon',
  pure: true,
  standalone: true,
})
export class GetIconPipe implements PipeTransform {
  transform(mean: MeanOfTransport, selectedMeans: MeanOfTransport[]): string {
    return selectedMeans.includes(mean) ? mean : `${mean}_GRAY`;
  }
}
