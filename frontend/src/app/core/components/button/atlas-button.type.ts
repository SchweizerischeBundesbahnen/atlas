export const AtlasButtonType = {
  CREATE: 'create' as AtlasButtonType,
  CREATE_CHECKING_PERMISSION: 'createCheckingPermission' as AtlasButtonType,
  FOOTER: 'footer' as AtlasButtonType,
  EDIT: 'edit' as AtlasButtonType,
  REVOKE: 'revoke' as AtlasButtonType,
  DELETE: 'delete' as AtlasButtonType,
  CLOSE_ICON: 'closeIcon' as AtlasButtonType,
};

export type AtlasButtonType =
  | 'createCheckingPermission'
  | 'create'
  | 'footer'
  | 'edit'
  | 'revoke'
  | 'delete';
