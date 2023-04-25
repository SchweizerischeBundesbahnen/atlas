export interface TableColumn<TYPE> {
  headerTitle: string;
  value?: keyof TYPE & string;
  disabled?: boolean;
  dropdown?: ColumnDropDown;
  checkbox?: ColumnCheckbox;
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

export interface ColumnCheckbox {
  changeSelectionCallback: (...args: any[]) => any;
}
