import { Moment } from 'moment';

export interface Validity {
  formValidTo: Moment | null | undefined;
  formValidFrom: Moment | null | undefined;
  initValidTo: Moment | null | undefined;
  initValidFrom: Moment | null | undefined;
}
