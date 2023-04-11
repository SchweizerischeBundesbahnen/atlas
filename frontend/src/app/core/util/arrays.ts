export function addElementsToArrayWhenNotUndefined<T>(...elements: Array<T | undefined>): T[] {
  return elements.filter((value): value is T => !!value);
}
