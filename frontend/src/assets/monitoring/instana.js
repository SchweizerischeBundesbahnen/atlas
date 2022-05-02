(function (s, t, a, n) {
  s[t] ||
    ((s[t] = a),
    (n = s[a] =
      function () {
        n.q.push(arguments);
      }),
    (n.q = []),
    (n.v = 2),
    (n.l = 1 * new Date()));
})(window, 'InstanaEumObject', 'ineum');

const instanaKey =
  typeof process === 'object' && process.env && process.env.INSTANA_KEY
    ? process.env.INSTANA_KEY
    : 'JxmTF5fGTEmLrJeyAiPtaA';

ineum('reportingUrl', 'https://eum-green-saas.instana.io');
ineum('key', instanaKey);
ineum('trackSessions');
