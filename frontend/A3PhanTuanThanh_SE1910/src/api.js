// Author: Phan Tuan Thanh - CE190595
import axios from 'axios';

export const api = axios.create({ baseURL: '/api', timeout: 10000 });
