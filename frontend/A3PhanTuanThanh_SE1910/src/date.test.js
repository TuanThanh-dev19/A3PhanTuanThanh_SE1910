// Author: Phan Tuan Thanh - CE190595
import test from 'node:test';
import assert from 'node:assert/strict';
import { localDate } from './date.js';

process.env.TZ = 'Asia/Bangkok';
test('Local date remains today just after midnight in Bangkok', () => {
  assert.equal(localDate(0, new Date('2026-11-03T17:30:00Z')), '2026-11-04');
});
test('Tomorrow advances the local calendar day across a year boundary', () => {
  assert.equal(localDate(1, new Date('2026-12-31T16:30:00Z')), '2027-01-01');
});
test('Yesterday respects leap-year February', () => {
  assert.equal(localDate(-1, new Date('2028-02-29T17:30:00Z')), '2028-02-29');
});
