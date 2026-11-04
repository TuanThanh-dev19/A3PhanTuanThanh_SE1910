// Author: Phan Tuan Thanh - CE190595
export function localDate(offset = 0, now = new Date()) {
  const date = new Date(now);
  date.setDate(date.getDate() + offset);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}
