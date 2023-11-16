import { ApplicationType } from '../../../api';
import { AtlasButtonType } from '../button/atlas-button.type';
import { Observable } from 'rxjs';

/* eslint-disable  @typescript-eslint/no-explicit-any */
export interface TableColumn<TYPE> {
  headerTitle: string;
  value?: keyof TYPE & string;
  disabled?: boolean;
  dropdown?: ColumnDropDown;
  checkbox?: ColumnCheckbox;
  button?: Button;
  callback?: (...args: any[]) => any;
  valuePath?: string;
  columnDef?: string;
  formatAsDate?: boolean;
  translate?: {
    withPrefix?: string;
    withKey?: string;
  };
  getTitle?: (value: string) => Observable<string>;
}

export interface ColumnDropDown {
  options: string[];
  disabled: boolean;
  changeSelectionCallback: (...args: any[]) => any;
  selectedOption: string;
  translate: {
    withPrefix: string;
  };
}

export interface ColumnCheckbox {
  changeSelectionCallback: (...args: any[]) => any;
}

export interface Button {
  label?: string;
  title?: string;
  icon?: string;
  clickCallback: (...args: any[]) => any;
  applicationType: ApplicationType;
  buttonDataCy: string;
  buttonType: AtlasButtonType;
  disabled: boolean;
}
