import { Line, Subline, Version } from '../../../api';

export interface TableSearch {
  searchCriteria?: string[];
  validOn?: Date;
  statusChoices?: statusChoice;
}

export type statusChoice = Version.StatusEnum[] | Subline.StatusEnum[] | Line.StatusEnum[];
