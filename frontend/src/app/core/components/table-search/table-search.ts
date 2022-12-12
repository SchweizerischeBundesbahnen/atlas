import { Status } from '../../../api';
import { BaseTableSearch } from './base-table-search';

export interface TableSearch extends BaseTableSearch {
  statusChoices?: statusChoice;
}

export type statusChoice = Status[];
