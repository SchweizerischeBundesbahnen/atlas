export interface TableColumn<TYPE> {
  headerTitle: string;
  value: keyof TYPE & string;
  formatAsDate?: boolean;
}
