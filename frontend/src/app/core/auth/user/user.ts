import { Permission } from '../../../api';

export interface User {
  email: string;
  name: string;
  sbbuid: string;
  isAdmin: boolean;
  permissions: Permission[];
}

export interface TokenUser extends User {
  preferred_username: string;
  roles: string[];
}
