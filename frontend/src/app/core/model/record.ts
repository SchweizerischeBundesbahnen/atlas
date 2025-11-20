import { Status } from '../../api';
import { CreationEditionRecord } from '../components/user-edit-info/creation-edition-record';

export interface Record extends CreationEditionRecord {
  id?: number;
  validFrom?: Date;
  validTo?: Date;
  slnid?: string;
  businessOrganisation?: string;
  status?: Status;
  versionNumber?: number;
}
