import { Status } from '../../../api';

export interface Record {
  id?: number;
  validFrom?: Date;
  validTo?: Date;
  slnid?: string;
  businessOrganisation?: string;
  status?: Status;
  versionNumber?: number;
  editor?: string;
  editionDate?: string;
  creator?: string;
  creationDate?: string;
}
