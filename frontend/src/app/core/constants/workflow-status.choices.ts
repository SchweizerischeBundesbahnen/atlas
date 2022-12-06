import { WorkflowStatus } from '../../api';

export const DEFAULT_WORKFLOW_STATUS_SELECTION = [
  WorkflowStatus.Added,
  WorkflowStatus.Started,
  WorkflowStatus.Approved,
  WorkflowStatus.Hearing,
  WorkflowStatus.Rejected,
  WorkflowStatus.Revision,
];
