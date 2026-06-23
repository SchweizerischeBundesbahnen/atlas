export interface ColumnDropDownEvent<ROW = unknown> {
  $event: { value: unknown[] };
  value: ROW;
}