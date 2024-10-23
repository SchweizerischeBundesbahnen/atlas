export interface DialogData {
  title: string;
  message: string;
  messageArgs?: { [key: string]: string };
  cancelText?: string;
  confirmText?: string;
  link?: { url: string, textLink: string, text: string };
  isInfo?: boolean;
}
