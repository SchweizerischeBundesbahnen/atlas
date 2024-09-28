import { Pipe, PipeTransform } from '@angular/core';
import { Parameter } from 'src/app/api';

@Pipe({
  name: 'paramsForTranslation',
  standalone: true,
})
export class ParamsForTranslationPipe implements PipeTransform {
  transform(paramList: Parameter[]): { [key: string]: string | undefined } {
    return paramList.reduce(
      (previousValue, currentValue) => {
        if (!currentValue.key) {
          return { ...previousValue };
        }
        return { ...previousValue, [currentValue.key]: currentValue.value };
      },
      {} as { [key: string]: string | undefined },
    );
  }
}
