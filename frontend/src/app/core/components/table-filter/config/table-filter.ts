export abstract class TableFilter<T> {
  row: number;
  elementWidthCssClass: string;

  constructor(row: number, elementWidthCssClass: string) {
    this.row = row;
    this.elementWidthCssClass = elementWidthCssClass;
  }

  protected abstract activeSearch: T;
  abstract getActiveSearch(): T;
}
