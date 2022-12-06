import { BaseTableSearch } from './base-table-search';
import { WorkflowStatus } from '../../../api';

export interface TableSearchWorkflow extends BaseTableSearch {
  statusChoices?: statusChoice;
}

export type statusChoice = WorkflowStatus[];
