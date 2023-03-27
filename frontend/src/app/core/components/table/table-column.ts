export interface TableColumn<TYPE> {
  headerTitle: string;
  value?: keyof TYPE & string;
  callback?: Function;
  valuePath?: string;
  columnDef?: string;
  formatAsDate?: boolean;
  translate?: {
    withPrefix?: string;
    withKey?: string;
  };
}
