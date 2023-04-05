import { BaseTableFilter } from './base-table-filter';
import { WorkflowStatus } from '../../../api';

export interface TableFilterWorkflow extends BaseTableFilter {
  statusChoices?: statusChoice;
}

export type statusChoice = WorkflowStatus[];
