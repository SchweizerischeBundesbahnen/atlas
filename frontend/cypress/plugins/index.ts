// Plugins enable you to tap into, modify, or extend the internal behavior of Cypress
// For more info, visit https://on.cypress.io/plugins-api
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import cypress_failed_log from 'cypress-failed-log/src/failed';
import cypress_high_resolution from 'cypress-high-resolution';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore

export default (on, config) => {
  on('task', {
    failed: cypress_failed_log(),
  });
  cypress_high_resolution(on, config);
};
