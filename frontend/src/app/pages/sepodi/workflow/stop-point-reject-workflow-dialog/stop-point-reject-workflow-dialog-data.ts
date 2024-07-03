import {DialogData} from "../../../../core/components/dialog/dialog.data";

export type RejectType = 'REJECT' | 'CANCEL';

export const RejectType = {
  Reject: 'REJECT' as RejectType,
  Cancel: 'CANCEL' as RejectType
};


export interface StopPointRejectWorkflowDialogData extends DialogData {
  workflowId: number;
  rejectType: RejectType;
}
