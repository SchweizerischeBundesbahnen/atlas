import { Status } from '../../../api';

export interface TableSearch {
  searchCriteria?: string[];
  validOn?: Date;
  statusChoices?: statusChoice;
}

export type statusChoice = Status[];
