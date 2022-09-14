export interface ReadOnlyData<T> {
  translationKey: string;
  value: keyof T;
  formatValue?: <P>(value: P) => string;
  valueDisplayClass?: string;
}
