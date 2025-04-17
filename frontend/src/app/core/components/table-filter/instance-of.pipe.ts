import { Pipe, PipeTransform } from '@angular/core';

// eslint-disable-next-line  @typescript-eslint/no-explicit-any
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
