export interface RgbPicker {
  value: string;
  onChangeColor: (color: string) => void;
  closeColorPickerDialog: ($event: KeyboardEvent) => void;
}
