export function addElementsToArrayWhenNotUndefined<T>(...elements: Array<T | undefined>): T[] {
  return elements.filter((value): value is T => !!value);
}

export function toNumberArrayStrict(input: string | string[] | undefined): number[] {
  if (input === undefined) {
    return [];
  }
  const arr = typeof input === 'string' ? [input] : input;
  return arr.map((s) => {
    const n = Number(s);
    if (Number.isNaN(n)) {
      throw new Error(`Invalid input`);
    }
    return n;
  });
}
