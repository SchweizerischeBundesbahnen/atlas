import { Status } from '../../../api';

export interface TableSearch {
  searchCriteria?: string[];
  validOn?: Date;
  statusChoices?: statusChoice;
  [key: string]: any;
}

export type statusChoice = Status[];
