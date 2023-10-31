export interface DialogData {
  title: string;
  message: string;
  messageArgs?: { [key: string]: string };
  cancelText?: string;
  confirmText?: string;
}
