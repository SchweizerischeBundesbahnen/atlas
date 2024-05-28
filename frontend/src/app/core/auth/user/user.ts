import {Permission} from "../../../api";

export interface User {
  email: string;
  name: string;
  sbbuid: string;
  isAdmin: boolean;
  permissions: Permission[];
}
