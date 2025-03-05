import CommonUtils from '../../../support/util/common-utils';

describe(
  'Unauthorized-CensoredUserData: Make sure user data is not visible on the unauthorized REST-APIs.',
  { testIsolation: false },
  () => {
    const censorStars = '*****';

    it('should censor business-organisation.contactEnterpriseEmail', () => {
      CommonUtils.getUnauthorized(
        '/business-organisation-directory/v1/business-organisations/versions/ch%3A1%3Asboid%3A101815'
      ).then((response) => {
        expect(response.status).to.eq(200);

        expect(response.body).to.be.an('array').of.length(1);
        expect(response.body[0])
          .property('contactEnterpriseEmail')
          .to.equal(censorStars);
      });
    });

    it('should censor user-administration.display-info', () => {
      const sbbUserId = 'u233445';
      CommonUtils.getUnauthorized(
        '/user-administration/v1/users/display-info?userIds=' + sbbUserId
      ).then((response) => {
        expect(response.status).to.eq(200);

        expect(response.body).to.be.an('array').of.length(1);
        expect(response.body[0]).property('sbbUserId').to.equal(sbbUserId);
        expect(response.body[0]).property('displayName').to.equal(censorStars);
      });
    });

    it('should censor user-administration.displayName', () => {
      const sbbUserId = 'fxatlsy';
      CommonUtils.getUnauthorized(
        '/user-administration/v1/users/' + sbbUserId + '/displayname'
      ).then((response) => {
        expect(response.status).to.eq(200);

        expect(response.body).to.exist.and.not.be.empty;
        expect(response.body).property('sbbUserId').to.equal(sbbUserId);
        expect(response.body).property('displayName').to.equal(censorStars);
      });
    });

    it('should censor user-administration.currentUser', () => {
      const sbbUserId = '8f7fbfa4-7dc6-48e0-ad70-fb01e7e7569a';
      CommonUtils.getUnauthorized('/user-administration/v1/users/current').then(
        (response) => {
          expect(response.status).to.eq(200);

          expect(response.body).to.exist.and.not.be.empty;
          expect(response.body).property('accountStatus').to.equal('DELETED');
          expect(response.body).property('sbbUserId').to.equal(sbbUserId);
          expect(response.body).property('userId').to.equal(sbbUserId);

          expect(response.body).property('mail').to.be.null;
          expect(response.body).property('lastName').to.be.null;
          expect(response.body).property('firstName').to.be.null;
          expect(response.body).property('displayName').to.be.null;

          expect(response.body).property('permissions').to.be.an('array').and
            .empty;
        }
      );
    });
  }
);
