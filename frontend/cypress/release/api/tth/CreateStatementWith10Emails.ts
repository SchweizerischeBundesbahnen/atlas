import TthUtils from "../../../support/util/tth-utils";
import CommonUtils from "../../../support/util/common-utils";

describe('TTH: Create a statement with several email-addresses', {testIsolation: false}, () => {

  const allStatusChoices = [TthUtils.status.PLANNED, TthUtils.status.ACTIVE, TthUtils.status.ARCHIVED].join(',');

  let timetableYear = -1;
  let sboid = "";
  let ttfnId = "";
  let statementId = -1;

  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1);
  const currentYear = today.getFullYear();


  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Close active year, if available', () => {
    CommonUtils.get('/line-directory/v1/timetable-hearing/years?statusChoices=' + TthUtils.status.ACTIVE)
      .then((response) => {
      expect(response.status).to.eq(200);
      expect(response).property('body').to.be.an('array');

      // When: There is a timetable hearing year active...
      if (response.body.length > 0) {
        expect(response.body.length).to.eq(TthUtils.ACTIVE_YEAR_MAX_COUNT);
        expect(response.body[0]).property('timetableYear').to.be.greaterThan(currentYear);

        // Then: ... close the active year
        CommonUtils.post('/line-directory/v1/timetable-hearing/years/' + response.body[0].timetableYear + '/close', {}).then((response) => {
          expect(response).property('status').to.equal(200);
        });
      }
    });
  });

  it('Step-3: Determine the next free timetable hearing year', () => {
    // Given: There is no active timetable hearing year
    CommonUtils.get('/line-directory/v1/timetable-hearing/years?statusChoices=' + allStatusChoices)
      .then((response) => {
      expect(response).property('status').to.equal(200);
      expect(response.body).to.be.an('array');

      const tthYears = response.body
      const activeYears = tthYears.filter(year => year.hearingStatus === TthUtils.status.ACTIVE).map(year => year.timetableYear);
      expect(activeYears.length, "There shouldn't be any active year.").to.eq(0);

      const usedYears = tthYears.filter(year =>
        (year.hearingStatus === TthUtils.status.PLANNED ||
          year.hearingStatus === TthUtils.status.ARCHIVED)).map(year => year.timetableYear);

      // The TTH-years are always one year later than the current one.
      timetableYear = currentYear + 1;

      // When: There is another next unused year...
      while (usedYears.includes(timetableYear)) {
        timetableYear++; // Then: Increment the year until we find one that's not used
      }
    });
  });

  it('Step-4: Create a timetable hearing year', () => {
      const hearingYear = timetableYear - 1;
      CommonUtils.post('/line-directory/v1/timetable-hearing/years', {
        timetableYear: timetableYear,
        hearingFrom: `${hearingYear}-05-01`,
        hearingTo: `${hearingYear}-05-31`,
        statementCreatableExternal: true,
        statementCreatableInternal: true,
        statementEditable: true
      }).then((response) => {
        expect(response).property('status').to.equal(201);
        expect(response).property('body').property('hearingStatus').to.eq(TthUtils.status.PLANNED);
      });
  });


  it('Step-5: Start the timetable hearing year', () => {
    CommonUtils.post(`/line-directory/v1/timetable-hearing/years/${timetableYear}/start`).then((response) => {
      expect(response).property('status').to.equal(200);
      expect(response).property('body').property('hearingStatus').to.eq(TthUtils.status.ACTIVE);
    });
  });

  it('Step-6: Create dependent Business Organisation', () => {
    const generateRandomString = (length) => {
      const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
      return Array.from({ length }, () => characters.charAt(Math.floor(Math.random() * characters.length))).join('');
    };

    CommonUtils.post("/business-organisation-directory/v1/business-organisations/versions", {
      descriptionDe: generateRandomString(10),
      descriptionFr: generateRandomString(10),
      descriptionIt: generateRandomString(10),
      descriptionEn: generateRandomString(10),
      abbreviationDe: generateRandomString(3),
      abbreviationFr: generateRandomString(3),
      abbreviationIt: generateRandomString(3),
      abbreviationEn: generateRandomString(3),
      organisationNumber: Cypress._.random(10000, 99999).toString(),
      validFrom: today.toISOString().split('T')[0],
      validTo: tomorrow.toISOString().split('T')[0]
    }).then((response) => {
      expect(response).property('status').to.equal(201);

      expect(response).property('body').property('sboid').to.exist.and.be.a('string');
      sboid = response.body.sboid;
    })
  });

  it('Step-7: Create dependent Time Table Field Number', () => {
    CommonUtils.post("/line-directory/v1/field-numbers/versions", {
      swissTimetableFieldNumber: Cypress._.random(10000, 99999).toString(),
      validFrom: today.toISOString().split('T')[0],
      validTo: tomorrow.toISOString().split('T')[0],
      businessOrganisation: sboid,
      number: Cypress._.random(10000, 99999).toString(),
    }).then((response) => {
      expect(response).property('status').to.equal(201);
      expect(response).property('body').property('ttfnid').to.exist.and.be.a('string');
      ttfnId = response.body.ttfnid;
    })
  });

  it('Step-8: Create statement with 10 email-addresses in active timetable hearing year', () => {
    TthUtils.postStatement("/line-directory/v2/timetable-hearing/statements", {
      ttfnid: ttfnId,
      statementSender: {
        emails: [
          "tmp1@sbb.ch",
          "tmp2@sbb.ch",
          "tmp3@sbb.ch",
          "tmp4@sbb.ch",
          "tmp5@sbb.ch",
          "tmp6@sbb.ch",
          "tmp7@sbb.ch",
          "tmp8@sbb.ch",
          "tmp9@sbb.ch",
          "tmp10@sbb.ch"
        ]
      },
      swissCanton: "BERN",
      statement: "I need some more busses please.",
      timetableYear: timetableYear,
    }).then((response) => {
      expect(response).property('status').to.equal(201);
    })
  });


  it('Step-9: Get statement values', () => {
    CommonUtils.get("/line-directory/v2/timetable-hearing/statements" +
      `?timetableHearingYear=${timetableYear}
      &statusRestrictions=RECEIVED
      &ttfnid=${ttfnId}`)
      .then((response) => {
        expect(response).property('status').to.equal(200);
        expect(response).property('body').property('objects').to.exist
          .and.to.be.an('array').that.has.lengthOf(1);

        const objects = response.body.objects[0]
        expect(objects).property('statementSender').to.exist
          .and.to.haveOwnProperty('emails').that.has.lengthOf(10);

        expect(objects).property('id').to.exist
        statementId = objects.id;

        expect(objects).property('timetableYear').to.eq(timetableYear);
      })
  });

  it('Step-10: Change statement status to PARTLY_IMPLEMENTED', () => {
    CommonUtils.put("/line-directory/v2/timetable-hearing/statements/update-statement-status", {
      "ids": [
        statementId
      ],
      "statementStatus": "PARTLY_IMPLEMENTED",
      "justification": "We are not done yet."
    }).then((response) => {
      expect(response).property('status').to.equal(200);
    })
  });

  it('Step-11: Close the timetable hearing year', () => {
    CommonUtils.post(`/line-directory/v1/timetable-hearing/years/${timetableYear}/close`).then((response) => {
      expect(response).property('status').to.equal(200);
      expect(response).property('body').property('hearingStatus').to.eq(TthUtils.status.ARCHIVED);
      expect(response).property('body').property('timetableYear').to.eq(timetableYear);
    });
  });

  it('Step-12: Check statement is in ARCHIVED timetable year', () => {
    CommonUtils.get(`/line-directory/v2/timetable-hearing/statements/${statementId}`)
      .then((response) => {
        expect(response).property('status').to.equal(200);
        expect(response).property('body').property('timetableYear').to.eq(timetableYear);
      })
  });
});
