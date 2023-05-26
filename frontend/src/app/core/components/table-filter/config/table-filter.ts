export abstract class TableFilter<T> {
  elementWidthCssClass: string;

  constructor(elementWidthCssClass: string) {
    this.elementWidthCssClass = elementWidthCssClass;
  }

  protected abstract activeSearch: T;
  abstract getActiveSearch(): T;
}
