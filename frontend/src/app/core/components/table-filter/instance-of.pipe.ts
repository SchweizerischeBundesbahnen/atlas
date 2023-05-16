import { Pipe, PipeTransform } from '@angular/core';

type AbstractType<T> = abstract new (...args: any[]) => T;

@Pipe({
  name: 'instanceOf',
  pure: true,
})
export class InstanceOfPipe implements PipeTransform {
  transform<V, R>(value: V, type: AbstractType<R>): R | undefined {
    return value instanceof type ? value : undefined;
  }
}
