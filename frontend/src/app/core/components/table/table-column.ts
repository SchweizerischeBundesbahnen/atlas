export interface TableColumn<TYPE> {
  headerTitle: string;
  value?: keyof TYPE & string;
  valuePath?: string;
  columnDef?: string;
  formatAsDate?: boolean;
  translate?: {
    withPrefix?: string;
    withKey?: string;
  };
}
