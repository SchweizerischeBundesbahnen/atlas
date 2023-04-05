export interface BaseTableFilter {
  searchCriteria?: string[];
  validOn?: Date;

  [key: string]: any;
}

export type SearchStatusType = 'DEFAULT_STATUS' | 'WORKFLOW_STATUS';
