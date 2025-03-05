import { Record } from '../../components/base-detail/record';
import { LineVersionWorkflow } from '../../../api';

export interface LineRecord extends Record {
  lineVersionWorkflows?: Set<LineVersionWorkflow>;
  number?: string;
}
