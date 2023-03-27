export interface TableColumn<TYPE> {
  headerTitle: string;
  value?: keyof TYPE & string;
  dropdown?: ColumnDropDown;
  callback?: (...args: any[]) => any;
  valuePath?: string;
  columnDef?: string;
  formatAsDate?: boolean;
  translate?: {
    withPrefix?: string;
    withKey?: string;
  };
}

export interface ColumnDropDown {
  options: string[];
  changeSelectionCallback: (...args: any[]) => any;
  selectedOption: string;
  translate: {
    withPrefix: string;
  };
}
