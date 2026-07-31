import { User } from '../../api';

/**
 * Single source of truth for resolving the mail address to actually use for a user: a manually
 * maintained override (see user administration) always wins over the mail address delivered by
 * Azure. Used by the user-select picker, the display pipe and the own-profile page.
 */
export class UserMailHelper {
  static effectiveMail(user: User | undefined | null): string | undefined {
    if (!user) {
      return undefined;
    }
    return user.manualMail && user.manualMail.trim().length > 0 ? user.manualMail : user.mail;
  }
}
