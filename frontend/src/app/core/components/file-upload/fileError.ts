export interface FileError {
  errorType: FileErrorType;
  file: File;
}

export enum FileErrorType {
  TYPE = 'TYPE',
  SIZE = 'SIZE',
}
