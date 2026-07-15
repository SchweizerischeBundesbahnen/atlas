export interface FieldExample {
  label?: string;
  translate?: boolean;
  numberOfChars?: number;
  arg?: Arg;
}

export interface Arg {
  key: string;
  value: string;
}
