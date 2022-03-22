import { Status } from '../../../api';

export interface Record {
  id?: number;
  validFrom?: Date;
  validTo?: Date;
  slnid?: string;
  status?: Status;
  description?: string;
  placeholder?: boolean;
}
