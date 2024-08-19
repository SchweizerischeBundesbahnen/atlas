import {DialogData} from "../../../../core/components/dialog/dialog.data";
import {ReadServicePointVersion, StopPointPerson} from "../../../../api";

export interface AddStopPointWorkflowDialogData extends DialogData {
  stopPoint: ReadServicePointVersion,
  examinants:  StopPointPerson[]
}
