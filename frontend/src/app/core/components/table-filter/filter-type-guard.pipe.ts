import { Pipe, PipeTransform } from '@angular/core';

export type TypeGuard<A, B extends A> = (aType: A) => aType is B;

@Pipe({
  name: 'filterTypeGuard',
})
export class FilterTypeGuardPipe implements PipeTransform {
  transform<A, B extends A>(value: A, typeGuard: TypeGuard<A, B>): B | undefined {
    return typeGuard(value) ? value : undefined;
  }
}
