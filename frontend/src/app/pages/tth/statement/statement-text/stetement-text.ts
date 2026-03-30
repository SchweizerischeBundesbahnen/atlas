export class StatementText {
  private _checkboxStatementAnonymous = false;
  private _showOriginalStatementText = false;
  private _showOriginalTextButton = false;
  private _originalTextButtonLabel = 'TTH.STATEMENT.SHOW_ORIGINAL_TEXT';
  private _originalTextButtonIcon = 'bi-lock-fill';
  private _currentAnonymousStatement!: string;

  get showOriginalStatementText(): boolean {
    return this._showOriginalStatementText;
  }

  set showOriginalStatementText(value: boolean) {
    this._showOriginalStatementText = value;
  }

  get originalTextButtonIcon(): string {
    return this._originalTextButtonIcon;
  }

  set originalTextButtonIcon(value: string) {
    this._originalTextButtonIcon = value;
  }

  get originalTextButtonLabel(): string {
    return this._originalTextButtonLabel;
  }

  set originalTextButtonLabel(value: string) {
    this._originalTextButtonLabel = value;
  }

  get showOriginalTextButton(): boolean {
    return this._showOriginalTextButton;
  }

  set showOriginalTextButton(value: boolean) {
    this._showOriginalTextButton = value;
  }

  get checkboxStatementAnonymous(): boolean {
    return this._checkboxStatementAnonymous;
  }

  set checkboxStatementAnonymous(value: boolean) {
    this._checkboxStatementAnonymous = value;
  }

  get currentAnonymousStatement(): string {
    return this._currentAnonymousStatement;
  }

  set currentAnonymousStatement(value: string) {
    this._currentAnonymousStatement = value;
  }
}
