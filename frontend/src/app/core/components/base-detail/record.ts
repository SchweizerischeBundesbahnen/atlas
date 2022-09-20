import { Status } from '../../../api';

export interface Record {
  id?: number;
  validFrom?: Date;
  validTo?: Date;
  slnid?: string;
  businessOrganisation?: string;
  status?: Status;
  versionNumber?: number;
}
