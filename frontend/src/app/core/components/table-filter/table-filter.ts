import { Status } from '../../../api';
import { BaseTableFilter } from './base-table-filter';

export interface TableFilter extends BaseTableFilter {
  statusChoices?: statusChoice;
}

export type statusChoice = Status[];
