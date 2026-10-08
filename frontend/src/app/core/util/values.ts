export function required<T>(value: T | null | undefined, message: string): T {
  if (value === null || value === undefined) {
    throw new Error(message);
  }
  return value;
}

export function applyIfDefined<T>(value: T | undefined, fn: (value: T) => void): void {
  if (value !== undefined) {
    fn(value);
  }
}
